package repository

import castToList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import data.CalendarEvent
import data.FriendRequest
import data.Friend
import data.Group
import data.Question
import data.Settings
import data.User
import kotlinx.coroutines.tasks.await
import resultCatching
import java.lang.StringBuilder
import java.security.SecureRandom
import java.util.*

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
@Suppress("TooManyFunctions", "LargeClass")
class FirebaseRepository(val db: FirebaseFirestore, val auth: FirebaseAuth) {

    companion object {
        private const val AUTO_ID_LENGTH = 28
        private const val AUTO_ID_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        private val rand: Random = SecureRandom()
    }

    //Set Functions
    suspend fun updateToken(token: String) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        } else {
            FirebaseFirestore.getInstance().collection("Users")
                .document(uid)
                .update("token", token)
                .await()
        }
    }

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

    /**
     * @param list: first string in pair is requesterId from friend request,
     * second string in pair is UID of FriendRequest pertaining to the requesterId
     */
    suspend fun addFriendDeleteFriendRequests(list: List<Pair<String, String>>) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        } else {
            val docRefsForDelete = list.map { l ->
                db.collection("FriendRequests")
                    .document(l.second)
            }
            val docRefsForUser = list.map { l ->
                db.collection("Friends")
                    .document(uid)
                    .collection("FriendList")
                    .document(l.first)
            }
            val docRefsForFriend = list.map { l ->
                db.collection("Friends")
                    .document(l.first)
                    .collection("FriendList")
                    .document(uid)
            }
            val docRefsForPrivateGroup = list.map {
                db.collection("Groups")
                    .document()
            }

            db.runBatch { batch ->
                docRefsForDelete.forEach { ref ->
                    batch.delete(ref)
                }
                docRefsForUser.forEachIndexed { index, ref ->
                    batch.set(ref, Friend(list[index].first))
                }
                docRefsForFriend.forEach { ref ->
                    batch.set(ref, Friend(uid))
                }
                docRefsForPrivateGroup.forEachIndexed { index, ref ->
                    batch.set(ref, Group("DM", listOf(uid, list[index].first), uid, true, emptyList()))
                }
            }.await()
        }
    }

    suspend fun sendFriendRequests(receivingIds: List<String>) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        } else {
            val requesterIds = db.collection("FriendRequests")
                .whereEqualTo("receiverId", uid)
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    doc.get("requesterId") as String?
                }
            val receiverIds = db.collection("FriendRequests")
                .whereEqualTo("requesterId", uid)
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    doc.get("receiverId") as String?
                }

            val docRefs = receivingIds.filter { it !in requesterIds && it !in receiverIds }.map {
                db.collection("FriendRequests")
                    .document()
            }

            val filteredFriendRequests = receivingIds.filter { it !in requesterIds }.map { id ->
                FriendRequest(uid, id)
            }

            db.runBatch { batch ->
                docRefs.forEachIndexed { index, ref ->
                    batch.set(ref, filteredFriendRequests[index])
                }
            }.await()
        }
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

    suspend fun batchUpdateUserCalendarEvent(calendarEvents: List<CalendarEvent>) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else {
            val docRefs = calendarEvents.map {
                db.collection("CalendarEvent")
                    .document(uid)
                    .collection("Events")
                    .document()
            }

            db.runBatch { batch ->
                docRefs.forEachIndexed { index, ref ->
                    batch.set(ref, calendarEvents[index])
                }
            }.await()
        }
    }

    /**
     * @param group: dm should equal to false when passing in Group,
     * owner should equal to current user's UID
     */
    suspend fun createGroup(group: Group) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else {
            val guid = autoId()
            val docRef = db.collection("Groups")
                .document(guid)

            val docRefs = group.members.map { id ->
                db.collection("Users")
                    .document(id)
            }

            db.runBatch { batch ->
                batch.set(docRef, group)
                docRefs.forEach { ref ->
                    batch.update(ref, "userGroups", FieldValue.arrayUnion(guid))
                }
            }.await()
        }
    }

    suspend fun addGroupMembers(uids: List<String>, guid: String) = resultCatching {
        val docRef1 = db.collection("Groups")
            .document(guid)
        val docRef2 = uids.map { id ->
            db.collection("Users")
                .document(id)
        }

        db.runBatch { batch ->
            uids.forEach { id ->
                batch.update(docRef1, "members",  FieldValue.arrayUnion(id))
            }
            docRef2.forEach { ref ->
                batch.update(ref, "userGroups", FieldValue.arrayUnion(guid))
            }
        }.await()
    }

    suspend fun updateGroupName(guid: String, newName: String) = resultCatching {
        val docRef1 = db.collection("Groups")
            .document(guid)

        db.runBatch { batch ->
            batch.update(docRef1, "groupName",  newName)
        }.await()
    }

    suspend fun addQuestionToDB(question: Question) = resultCatching {
        db.collection("Questions")
            .document()
            .set(question)
            .await()
        question
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
            User(data.get("school") as String?,
                data.get("program") as String?,
                data.get("interests") as List<String>?,
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
        User(data.get("school") as String?,
            data.get("program") as String?,
            data.get("interests") as List<String>?,
            data.get("name") as String?,
            data.get("token") as String?,
            (data.get("userGroups") as? List<*>).castToList(),
            uid = data.id)
    }

    suspend fun getListOfUserInfo(uids: List<String>) = resultCatching {
        db.collection("Users")
            .whereIn(FieldPath.documentId(), uids)
            .get()
            .await()
            .map { doc -> User(
                doc.get("school") as String?,
                doc.get("program") as String?,
                doc.get("interests") as List<String>?,
                doc.get("name") as String?,
                doc.get("token") as String?,
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

    suspend fun getRelevantCalendarEvents(startTimestampMS: Long, endTimestampMS: Long) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        } else {
            val events1 = db.collection("CalendarEvent")
                .document(uid)
                .collection("Events")
                .whereGreaterThanOrEqualTo("startTime", startTimestampMS)
                .whereLessThanOrEqualTo("startTime", endTimestampMS)
                .get()
                .await()
                .documents.map { doc -> CalendarEvent(
                    doc.get("name") as String,
                    doc.get("startTime") as Long,
                    doc.get("endTime") as Long,
                    doc.get("allDay") as Boolean,
                    doc.get("recurringEvent") as String?,
                    doc.get("recurringEnd") as Long?,
                    doc.get("minutesBefore") as Long,
                    uid = doc.id)
                }
            val events2 = db.collection("CalendarEvent")
                .document(uid)
                .collection("Events")
                .whereIn("recurringEvent", listOf("Daily", "Weekly", "Monthly"))
                .get()
                .await()
                .documents.map { doc -> CalendarEvent(
                    doc.get("name") as String,
                    doc.get("startTime") as Long,
                    doc.get("endTime") as Long,
                    doc.get("allDay") as Boolean,
                    doc.get("recurringEvent") as String?,
                    doc.get("recurringEnd") as Long?,
                    doc.get("minutesBefore") as Long,
                    uid = doc.id)
                }
            (events1 + events2).distinct()
        }
    }

    suspend fun getSpecificGroupTypes(guid: String) = resultCatching {
        val data = db.collection("Groups")
            .document(guid)
            .get()
            .await()
        data.get("groupTypes") as List<String>
    }

    suspend fun getRelevantCalendarEventsForUser(uid: String, startTimestampMS: Long,
                                                 endTimestampMS: Long) = resultCatching {
        val events1 = db.collection("CalendarEvent")
            .document(uid)
            .collection("Events")
            .whereGreaterThanOrEqualTo("startTime", startTimestampMS)
            .whereLessThanOrEqualTo("startTime", endTimestampMS)
            .get()
            .await()
            .documents.map { doc -> CalendarEvent(
                doc.get("name") as String,
                doc.get("startTime") as Long,
                doc.get("endTime") as Long,
                doc.get("allDay") as Boolean,
                doc.get("recurringEvent") as String?,
                doc.get("recurringEnd") as Long?,
                doc.get("minutesBefore") as Long,
                uid = doc.id)
            }
        val events2 = db.collection("CalendarEvent")
            .document(uid)
            .collection("Events")
            .whereIn("recurringEvent", listOf("Daily", "Weekly", "Monthly"))
            .get()
            .await()
            .documents.map { doc -> CalendarEvent(
                doc.get("name") as String,
                doc.get("startTime") as Long,
                doc.get("endTime") as Long,
                doc.get("allDay") as Boolean,
                doc.get("recurringEvent") as String?,
                doc.get("recurringEnd") as Long?,
                doc.get("minutesBefore") as Long,
                uid = doc.id)
            }
        (events1 + events2).distinct()
    }

    @SuppressWarnings("LongMethod")
    suspend fun getAllUserGroups() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else {
            val groups = db.collection("Groups")
                .whereArrayContains("members", uid)
                .get()
                .await()
                .documents.map { doc ->
                    Group(
                        doc.get("groupName") as String,
                        (doc.get("members") as? List<*>).castToList(),
                        doc.get("owner") as String,
                        doc.get("dm") as Boolean,
                        (doc.get("groupTypes") as? List<*>).castToList(),
                        uid = doc.id
                    )
                }
            val map1 = mutableMapOf<String?, String?>()
            val map2 = mutableMapOf<String?, User?>()
            val friends = groups.filter { it.dm }.mapNotNull { g ->
                if (g.members.size == 2) {
                    if (g.members[0] != uid) {
                        map1[g.uid] = g.members[0]
                        g.members[0]
                    } else {
                        map1[g.uid] = g.members[1]
                        g.members[1]
                    }
                } else {
                    null
                }
            }
            if (friends.isNotEmpty()) {
                val friendInfos = getListOfUserInfo(friends)
                if (friendInfos.exception != null) {
                    throw friendInfos.exception
                }
                if (friendInfos.data == null) {
                    throw NoDataException
                }
                friendInfos.data.forEach { user ->
                    map1.forEach { item ->
                        if (item.value == user.uid) {
                            map2[item.key] = user
                        }
                    }
                }
            }

            val retPair =  mutableListOf<Pair<User?, Group>>()
            groups.forEach { g ->
                if (g.dm) {
                    retPair.add(Pair(map2[g.uid], g))
                }
            }
            groups.forEach { g ->
                if (!g.dm) {
                    retPair.add(Pair(null, g))
                }
            }
            retPair
        }
    }

    suspend fun getUserFriendRequests() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        }
        else {
            val requests = db.collection("FriendRequests")
                .whereEqualTo("receiverId", uid)
                .get()
                .await()
                .documents.map { doc ->
                    FriendRequest(
                        doc.get("requesterId") as String?,
                        doc.get("receiverId") as String?,
                        uid = doc.id
                    )
                }
            val map = mutableMapOf<String?, String?>()
            val userIds = requests.mapNotNull {
                map[it.requesterId] = it.uid
                it.requesterId
            }
            val users = getListOfUserInfo(userIds)
            if (users.exception != null) {
                throw users.exception
            }
            if (users.data == null) {
                throw NoDataException
            }

            val listOfUser = users.data
            listOfUser.map {
                Pair(it, map[it.uid])
            }
        }
    }

    suspend fun searchNonFriendUsersWithName(userName: String) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        }
        else {
            val friends = db.collection("Friends")
                .document(uid)
                .collection("FriendList")
                .get()
                .await()
                .documents.map { doc ->
                    doc.id
                }

            val searchIds = db.collection("Users")
                .whereGreaterThanOrEqualTo("name", userName)
                .whereLessThanOrEqualTo("name", userName + '\uf8ff')
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    if (doc.id != getCurrentUserId()) {
                        doc.id
                    } else {
                        null
                    }
                }

            val userIds = searchIds.filter { it !in friends }.map { id ->
                id
            }

            db.collection("Users")
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .await()
                .documents.map { doc ->
                    User(doc.get("school") as String?,
                        doc.get("program") as String?,
                        doc.get("interests") as List<String>?,
                        doc.get("name") as String?,
                        doc.get("token") as String?,
                        (doc.get("userGroups") as? List<*>).castToList(),
                        uid = doc.id)
                }
        }
    }

    suspend fun searchFriendsWithName(userName: String) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null) {
            throw NoUserUIDException
        }
        else {
            val friends = db.collection("Friends")
                .document(uid)
                .collection("FriendList")
                .get()
                .await()
                .documents.map { doc ->
                    doc.id
                }

            val searchIds = db.collection("Users")
                .whereGreaterThanOrEqualTo("name", userName)
                .whereLessThanOrEqualTo("name", userName + '\uf8ff')
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    if (doc.id != getCurrentUserId()) {
                        doc.id
                    } else {
                        null
                    }
                }

            val userIds = searchIds.filter { it in friends }.map { id ->
                id
            }

            db.collection("Users")
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .await()
                .documents.map { doc ->
                    User(doc.get("school") as String?,
                        doc.get("program") as String?,
                        doc.get("interests") as List<String>?,
                        doc.get("name") as String?,
                        doc.get("token") as String?,
                        (doc.get("userGroups") as? List<*>).castToList(),
                        uid = doc.id)
                }
        }
    }

    suspend fun getQuestionFromDB(id: String) = resultCatching {
        val data = db.collection("Questions")
            .document(id)
            .get()
            .await()
        Question(data.get("question") as String,
            (data.get("answers") as List<*>).castToList(),
            (data.get("answerIndexes") as List<*>).castToList(),
            data.get("questionType") as String?,
            uid = data.id)
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

    suspend fun deleteUserCalendarEventForUser(uid: String, cid: String) = resultCatching {
        db.collection("CalendarEvent")
            .document(uid)
            .collection("Events")
            .document(cid)
            .delete()
            .await()
    }

    suspend fun removeGroupMember(uid: String, guid: String) = resultCatching {
        val docRef1 = db.collection("Groups")
            .document(guid)
        val docRef2 = db.collection("Users")
            .document(uid)

        db.runBatch { batch ->
            batch.update(docRef1, "members",  FieldValue.arrayRemove(uid))
            batch.update(docRef2, "userGroups", FieldValue.arrayRemove(guid))
        }.await()
    }

    suspend fun deleteGroup(guid: String, members: List<String>) = resultCatching {
        val docRef1 = db.collection("Groups")
            .document(guid)
        val docRef2 = members.map { id ->
            db.collection("Users")
                .document(id)
        }

        db.runBatch { batch ->
            batch.delete(docRef1)
            docRef2.forEach { ref ->
                batch.update(ref, "userGroups", FieldValue.arrayRemove(guid))
            }
        }.await()
    }

    suspend fun removeUserFriend(fuid: String, guid: String) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else {
            val docRef1 = db.collection("Friends").document(uid).collection("FriendList").document(fuid)
            val docRef2 = db.collection("Friends").document(fuid).collection("FriendList").document(uid)

            val docRefGroup = db.collection("Groups").document(guid)

            db.runBatch { batch ->
                batch.delete(docRef1)
                batch.delete(docRef2)
                batch.delete(docRefGroup)
            }.await()
        }
    }

    //Helper functions
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    @SuppressWarnings("UnusedPrivateMember")
    private fun autoId(): String {
        val builder = StringBuilder()
        val maxRandom = AUTO_ID_ALPHABET.length
        for (i in 0 until AUTO_ID_LENGTH) {
            builder.append(AUTO_ID_ALPHABET[rand.nextInt(maxRandom)])
        }
        return builder.toString()
    }
}

object NoUserUIDException: Exception()
object NoDataException: Exception()
object NoCalendarEventUIDException: Exception()
object FriendNotFoundException: Exception()
object MissingGroupInformationException: Exception()
