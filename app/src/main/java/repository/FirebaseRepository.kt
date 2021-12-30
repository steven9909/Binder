package repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import data.Friends
import data.Settings
import data.User

class FirebaseRepository(val db: FirebaseFirestore, val auth: FirebaseAuth) {

    val failMsg = "Current User UID not found"

    fun updateBasicUserInformation(user: User): Result<Void> {
        return Result(true, task = db.collection("Users").document(user.userId).set(user))
    }

    fun updateGeneralUserSettings(settings: Settings): Result<Void> {
        getCurrentUserId()?.let { uid ->
            return Result(true, task = db.collection("Settings").document(uid).set(settings))
        }
        return Result(false, failMsg)
    }

    fun updateUserFriendList(friends: Friends): Result<Void> {
        getCurrentUserId()?.let { uid ->
            return Result(true, task = db.collection("Friends").document(uid).set(friends, SetOptions.merge()))
        }
        return Result(false, failMsg)
    }

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid;
    }
}

class Result<T>(val isSuccessful: Boolean, val cause: String? = null, val task: Task<T>? = null) {

}