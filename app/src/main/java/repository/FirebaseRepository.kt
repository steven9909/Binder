package repository

import castToList
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import data.CalendarEvent
import data.FriendRequest
import data.Friend
import data.Group
import data.Settings
import data.User
import kotlinx.coroutines.tasks.await
import resultCatching

/**
 * API Functions to interact with the Firebase database
 * Includes get, set and delete functions to be used in respective ViewModels
 *
 * API Functions available for the following data models:
 * @see User
 * @see Settings
 * @see Friend
 * @see FriendRequest
 * @see CalendarEvent
 * @see Group
 */
@Suppress("TooManyFunctions")
class FirebaseRepository(val db: FirebaseFirestore, val auth: FirebaseAuth) {

    //Set Functions
    suspend fun updateBasicUserInformation(user: User) = resultCatching {
        if (user.uid != null) {
            db.collection("Users")
                .document(user.uid)
                .set(user)
                .await()
        } else {
            throw NoUserUIDException
        }
    }

    suspend fun updateUserToken(uid: String, token: String) = resultCatching {
        db.collection("Users")
            .document(uid)
            .update("token", token)
            .await()
    }

    suspend fun updateUserGroupsField(uid:String, userGroups: List<String>) = resultCatching {
        db.collection("Users")
            .document(uid)
            .update("userGroups", userGroups)
            .await()
    }

    suspend fun updateGeneralUserSettings(settings: Settings) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Settings")
                .document(uid)
                .set(settings)
                .await()
    }

    suspend fun addFriend(friend: Friend) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null || friend.uid == null) {
            throw NoUserUIDException
        } else {
            val docRef1 = db.collection("Friends").document(uid).collection("FriendList").document(friend.uid)
            val docRef2 = db.collection("Friends").document(friend.uid).collection("FriendList").document(uid)

            db.runBatch { batch ->
                batch.set(docRef1, friend)
                batch.set(docRef2, Friend(uid))
            }.await()
        }
    }

    suspend fun sendFriendRequests(friendRequests: List<FriendRequest>) = resultCatching {
        val docRefs = friendRequests.mapNotNull {
            it.receivingId?.let { receivingId ->
                it.requesterId?.let { requesterId ->
                    db.collection("FriendRequests").document(receivingId).collection("Requests")
                        .document(it.requesterId)
                }
            }
        }
        db.runBatch { batch ->
            docRefs.forEachIndexed { index, ref ->
                batch.set(ref, friendRequests[index])
            }
        }.await()
    }

    suspend fun updateUserCalendarEvent(calendarEvent: CalendarEvent) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("CalendarEvent")
                .document(uid)
                .collection("Events")
                .document()
                .set(calendarEvent)
                .await()
    }

    suspend fun createGroup(group: Group) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Groups")
                .document()
                .set(group)
                .await()
    }

    suspend fun addGroupMember(guid:String, member: String) = resultCatching {
        db.collection("Groups")
            .document(guid)
            .update("members", FieldValue.arrayUnion(member))
            .await()
    }

    //Get Functions
    suspend fun getBasicUserInformation() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        } else {
            val data = db.collection("Users")
                .document(uid)
                .get()
                .await()
            User(data.get("school") as String,
                data.get("program") as String,
                data.get("interests") as String,
                data.get("name") as String?,
                data.get("token") as String?,
                (data.get("userGroups") as? List<*>).castToList(),
                uid = data.id)
        }
    }

    suspend fun getSpecificUserInformation(uid: String) = resultCatching {
        val data = db.collection("Users")
            .document(uid)
            .get()
            .await()
        User(data.get("school") as String,
            data.get("program") as String,
            data.get("interests") as String,
            data.get("name") as String,
            data.get("token") as String,
            (data.get("userGroups") as? List<*>).castToList(),
            uid = data.id)
    }

    suspend fun getListOfUserInfo(uids: List<String>) = resultCatching {
        db.collection("Users")
            .whereIn(FieldPath.documentId(), uids)
            .get()
            .await()
            .map { doc -> User(
                doc.get("school") as String,
                doc.get("program") as String,
                doc.get("interests") as String,
                doc.get("name") as String,
                doc.get("token") as String,
                (doc.get("userGroups") as? List<*>).castToList(),
                uid = doc.id)
            }
    }

    suspend fun getBasicUserSettings() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        } else {
            val data = db.collection("Settings")
                .document(uid)
                .get()
                .await()
            Settings(data.get("enableNotifications") as Boolean,
                data.get("language") as String,
                data.get("customStatus") as String,
                uid = data.id)
        }
    }

    suspend fun getBasicUserFriends() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Friends")
                .document(uid)
                .collection("FriendList")
                .get()
                .await()
                .documents.map { doc -> Friend(
                    uid = doc.id)
                }
    }

    suspend fun getAdvancedUserFriends() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        } else {
            val friends = db.collection("Friends")
                .document(uid)
                .collection("FriendList")
                .get()
                .await()
                .documents.map { doc ->
                    Friend(
                        uid = doc.id
                    )
                }.mapNotNull {
                    it.uid
                }
            val users = getListOfUserInfo(friends)
            if (users.exception != null) {
                throw users.exception
            }
            if (users.data == null) {
                throw NoDataException
            }
            users.data
        }
    }

    suspend fun getUserCalendarEvents() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("CalendarEvent")
                .document(uid)
                .collection("Events")
                .get()
                .await()
                .documents.map { doc -> CalendarEvent(
                    doc.get("name") as String,
                    doc.get("startTime") as Timestamp,
                    doc.get("endTime") as Timestamp,
                    doc.get("allDay") as Boolean,
                    doc.get("recurringEvent") as String,
                    doc.get("minutesBefore") as Long,
                    uid = doc.id)
                }
    }

    suspend fun getAllUserGroups() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Groups")
                .whereArrayContains("members", uid)
                .get()
                .await()
                .documents.map{ doc -> Group(
                    doc.get("groupName") as String,
                    (doc.get("members") as? List<*>).castToList(),
                    uid = doc.id)
                }
    }

    suspend fun getUserFriendRequests() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        }
        else {
            val requests = db.collection("FriendRequests")
                .document(uid)
                .collection("Requests")
                .get()
                .await()
                .documents.map { doc ->
                    FriendRequest(
                        doc.get("requesterId") as String,
                        doc.get("receivingId") as String,
                        uid = doc.id
                    )
                }
            val userIds = requests.mapNotNull {
                it.requesterId
            }
            val users = getListOfUserInfo(userIds)
            if (users.exception != null) {
                throw users.exception
            }
            if (users.data == null) {
                throw NoDataException
            }
            users.data
        }
    }

    suspend fun searchUsersWithName(userName: String) = resultCatching {
        db.collection("Users")
            .whereGreaterThanOrEqualTo("name", userName)
            .whereLessThanOrEqualTo("name", userName + '\uf8ff')
            .get()
            .await()
            .documents.mapNotNull { doc ->
                if (doc.id != getCurrentUserId()) {
                    User(doc.get("school") as String?,
                        doc.get("program") as String?,
                        doc.get("interests") as String?,
                        doc.get("name") as String?,
                        doc.get("token") as String?,
                        (doc.get("userGroups") as? List<*>).castToList(),
                        uid = doc.id)
                } else {
                    null
                }
            }
    }

    //Delete Functions
    suspend fun deleteUserCalendarEvent(cid: String?) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        }
        else if (cid == null) {
            throw NoCalendarEventUIDException
        }
        else {
            db.collection("CalendarEvent")
                .document(uid)
                .collection("Events")
                .document(cid)
                .delete()
                .await()
        }
    }

    suspend fun deleteFriendRequest(ruid: String) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("FriendRequests")
                .document(uid)
                .collection("Requests")
                .document(ruid)
                .delete()
                .await()
    }

    suspend fun deleteGroupMember(guid:String, member: String) = resultCatching {
        db.collection("Groups")
            .document(guid)
            .update("members", FieldValue.arrayRemove(member))
            .await()
    }

    suspend fun removeUserFriend(fuid: String) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else {
            val docRef1 = db.collection("Friends").document(uid).collection("FriendList").document(fuid)
            val docRef2 = db.collection("Friends").document(fuid).collection("FriendList").document(uid)

            db.runBatch { batch ->
                batch.delete(docRef1)
                batch.delete(docRef2)
            }.await()
        }
    }
    
    //Helper functions
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}

object NoUserUIDException: Exception()
object NoDataException: Exception()
object NoCalendarEventUIDException: Exception()
