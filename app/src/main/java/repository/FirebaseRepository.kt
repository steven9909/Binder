package repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    fun updateGeneralUserSettings(settings: Settings) = resultCatching {
        val uid = getCurrentUserId()
        if (uid == null)
            throw NoUserUIDException
        else
            db.collection("Settings").document(uid).set(settings)

    }

    /**
    @TODO rewrite!
    fun updateUserFriendList(friends: Friends): Result<Void> {
        getCurrentUserId()?.let { uid ->
            return Result(true, task = db.collection("Friends").document(uid).set(friends, SetOptions.merge()))
        }
        return Result(false, FAILED_TO_FIND_USER_UID)
    }

    fun updateUserCalendarEvent(calendarEvent: CalendarEvent): Result<Void> {
        getCurrentUserId()?.let { uid ->
            return Result(true, task = db.collection("CalendarEvent").document(uid).set(calendarEvent))
        }
        return Result(false, FAILED_TO_FIND_USER_UID)
    }

    //Get Functions
    fun getBasicUserInformation(): MutableLiveData<User> {
        val mutableLiveData = MutableLiveData<User>()
        getCurrentUserId()?.let { uid ->
            db.collection("Users").document(uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mutableLiveData.postValue(task.result.toObject<User>())
                    }
                }
        }
        return mutableLiveData
    }
    */

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}

object NoUserUIDException: Exception()
