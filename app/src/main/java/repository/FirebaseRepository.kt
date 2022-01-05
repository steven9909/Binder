package repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import data.CalendarEvent
import data.Friends
import data.Group
import data.Settings
import data.User
import kotlinx.coroutines.tasks.await
import resultCatching

class FirebaseRepository(val db: FirebaseFirestore, val auth: FirebaseAuth) {

    //Set Functions
    suspend fun updateBasicUserInformation(user: User) = resultCatching {
        db.collection("Users").document(user.userId).set(user).await()
    }

    suspend fun updateGeneralUserSettings(settings: Settings) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Settings").document(uid).set(settings).await()
    }

    suspend fun updateUserFriendList(friends: Friends) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Friends").document(uid).set(friends, SetOptions.merge()).await()
    }

    suspend fun updateUserCalendarEvent(calendarEvent: CalendarEvent) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("CalendarEvent").document(uid).collection("Events").document().set(calendarEvent).await()
    }

    suspend fun updateGroup(group: Group) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("UserGroups").document(uid).collection("Groups").document().set(group).await()
    }

    //Get Functions
    suspend fun getBasicUserInformation() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Users").document(uid).get().await().toObject<User>()
    }

    suspend fun getBasicUserSettings() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Settings").document(uid).get().await().toObject<Settings>()
    }

    suspend fun getBasicUserFriends() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Friends").document(uid).get().await().toObject<Friends>()
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

    suspend fun getUserGroups() = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("UserGroups")
                .document(uid)
                .collection("Groups")
                .get()
                .await()
                .documents.map { doc -> Group(
                    doc.get("groupName") as String,
                    listOf(doc.get("people") as Friends))
                }
    }

    suspend fun getUserGroupUID(index: Int) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("UserGroups")
                .document(uid)
                .collection("Groups")
                .get()
                .await()
                .documents[index].id
    }

    suspend fun getSpecificUserGroup(groupID:String) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("UserGroups")
                .document(uid)
                .collection("Groups")
                .document(groupID)
                .get()
                .await()
                .toObject<Group>()
    }
    
    //Helper functions
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}

object NoUserUIDException: Exception()
