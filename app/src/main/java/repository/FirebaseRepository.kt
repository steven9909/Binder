package repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
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

    /**
    @TODO rewrite!

    fun updateGeneralUserSettings(settings: Settings): Result<Void> {
        getCurrentUserId()?.let { uid ->
            return Result(true, task = db.collection("Settings").document(uid).set(settings))
        }
        return Result(false, FAILED_TO_FIND_USER_UID)
    }

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
