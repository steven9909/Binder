package repository

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase
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
            .addChildEventListener(eventListener)
    }

    fun removeChildEventListenerForMessage(uid: String, eventListener: ChildEventListener) {
        db.getReference(MESSAGES)
            .child(uid)
            .removeEventListener(eventListener)
    }

    suspend fun getMoreMessages(uid: String, lastMessage: Message) = resultCatching {
        db.getReference(MESSAGES)
            .child(uid)
            .orderByChild("sentTime")
            .endAt(lastMessage.sentTime.toString())
            .limitToLast(PAGE_SIZE)
            .get()
            .await()
            .children.mapNotNull { child ->
                if (child.value != null) {
                    child.getValue(Message::class.java)
                } else {
                    null
                }
            }
    }
}

object GetMessageFailException: Exception()
