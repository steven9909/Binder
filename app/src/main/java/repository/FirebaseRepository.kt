package repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import data.CalendarEvent
import data.Friends
import data.Settings
import data.User
import kotlinx.coroutines.tasks.await
import resultCatching

class FirebaseRepository(val db: FirebaseFirestore, val auth: FirebaseAuth) {

    companion object {
        private const val FAILED_TO_FIND_USER_UID = "Current User UID not found"
        private const val FAILED_GET_TASK = "Get Task Failed"
    }

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
            db.collection("CalendarEvent").document(uid).set(calendarEvent).await()
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
            db.collection("CalendarEvent").document(uid).get().await().toObject<CalendarEvent>()
    }
    
    //Helper functions
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}

object NoUserUIDException: Exception()
