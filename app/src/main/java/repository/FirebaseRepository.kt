package repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import data.User

class FirebaseRepository(val db: FirebaseFirestore) {

    fun updateBasicUserInformation(user: User): Task<Void> {
        return db.collection("Users").document(user.userId).set(user)
    }
}