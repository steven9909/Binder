package repository

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import data.Message
import kotlinx.coroutines.tasks.await
import resultCatching

class RealtimeDB(val db: FirebaseDatabase) {

    companion object {
        private const val MESSAGES = "Messages"
        private const val PAGE_SIZE = 50
    }

    suspend fun sendMessage(message: Message, uid: String) = resultCatching {
        db.getReference(MESSAGES)
            .child(uid)
            .push()
            .setValue(message)
            .await()
    }

    fun getMessage(uid: String, eventListener: ChildEventListener) = resultCatching {
        db.getReference(MESSAGES)
            .child(uid)
            .orderByChild("timestamp")
            .limitToLast(PAGE_SIZE)
            .addChildEventListener(eventListener)
    }

    fun removeChildEventListenerForMessage(uid: String, eventListener: ChildEventListener) {
        db.getReference(MESSAGES)
            .child(uid)
            .removeEventListener(eventListener)
    }

    fun getMoreMessages(uid: String, lastMessageTimestamp: Long, valueEventListener: ValueEventListener) = resultCatching {
        db.getReference(MESSAGES)
            .child(uid)
            .orderByChild("timestamp")
            .endAt(lastMessageTimestamp.toDouble()-1)
            .limitToLast(PAGE_SIZE)
            .addListenerForSingleValueEvent(valueEventListener)
    }
}

object GetMessageFailException: Exception()
