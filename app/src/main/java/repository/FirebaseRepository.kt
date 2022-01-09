package repository

import castToList
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
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
        user.uid?.let {
            db.collection("Users")
                .document(user.uid)
                .set(user)
                .await()
        } ?: throw NoUserUIDException
    }

    suspend fun updateUserToken(uid: String, token: String) = resultCatching {
        db.collection("Users")
            .document(uid)
            .update("token", token)
            .await()
    }

    suspend fun updateUserGroupsField(uid:String, userGroups: List<Group>) = resultCatching {
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

    suspend fun addFriend(userToFriend: Friend, friendToUser: Friend) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else {
            db.collection("Friends")
                .document(uid)
                .collection("FriendList")
                .document()
                .set(userToFriend)
                .await()
            db.collection("Friends")
                .document(userToFriend.friendId)
                .collection("FriendList")
                .document()
                .set(friendToUser)
                .await()
        }
    }

    suspend fun sendFriendRequests(friendRequests: List<FriendRequest>) = resultCatching {
        val docRefs = friendRequests.mapNotNull {
            it.receivingId?.let { receivingId ->
                db.collection("FriendRequests").document(receivingId).collection("Requests")
                    .document()
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
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Users")
                .document(uid)
                .get()
                .await()
                .toObject<User>()
    }

    suspend fun getSpecificUserInformation(uid: String) = resultCatching {
        db.collection("Users")
            .document(uid)
            .get()
            .await()
            .toObject<User>()
    }

    suspend fun getListOfUserInfo(uids: List<String>) = resultCatching {
        db.collection("Users")
            .whereIn("userId", uids)
            .get()
            .await()
            .documents.map { doc ->
                doc.toObject<User>()
            }
    }

    suspend fun getBasicUserSettings() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Settings")
                .document(uid)
                .get()
                .await()
                .toObject<Settings>()
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
                .documents.map { doc -> Friend(doc.get("friendId") as String)
                }
    }

    suspend fun getFriendFUID(friendId: String) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Friends")
                .document(friendId)
                .collection("FriendList")
                .whereEqualTo("friendId", uid)
                .limit(1)
                .get()
                .await()
                .documents.map { doc ->
                    doc.id
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
                    doc.get("minutesBefore") as Long)
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
                    (doc.get("members") as? List<*>).castToList())
                }
    }

    suspend fun getUserFriendRequests() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("FriendRequests")
                .document(uid)
                .collection("Requests")
                .get()
                .await()
                .documents.map { doc -> FriendRequest(
                    doc.get("requesterId") as String,
                    doc.get("receivingId") as String)
                }
    }

    suspend fun searchUsersWithName(userName: String) = resultCatching {
        db.collection("Users")
            .whereGreaterThanOrEqualTo("name", userName)
            .whereLessThanOrEqualTo("name", userName + '\uf8ff')
            .get()
            .await()
            .documents.map { doc -> User(
                doc.get("userId") as String,
                doc.get("school") as String,
                doc.get("program") as String,
                doc.get("interests") as String,
                doc.get("name") as String)
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

    suspend fun removeUserFriend(fuid: String, friendId: String, friendFUID: String) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else {
            db.collection("Friends")
                .document(uid)
                .collection("FriendList")
                .document(fuid)
                .delete()
                .await()
            db.collection("Friends")
                .document(friendId)
                .collection("FriendList")
                .document(friendFUID)
                .delete()
                .await()
        }
    }
    
    //Helper functions
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}

object NoUserUIDException: Exception()
object NoCalendarEventUIDException: Exception()
