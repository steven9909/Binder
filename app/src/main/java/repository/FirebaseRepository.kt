package repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import data.User

class FirebaseRepository(val db: FirebaseFirestore) {

    fun updateBasicUserInformation(user: User): Result<Void> {
        return Result(true, task = db.collection("Users").document(user.userId).set(user))
    }
}

class Result<T>(val isSuccessful: Boolean, val cause: String? = null, val task: Task<T>? = null) {

}