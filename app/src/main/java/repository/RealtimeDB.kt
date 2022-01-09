package repository

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import data.Message
import kotlinx.coroutines.flow.callbackFlow
import resultCatching
import Result
import kotlinx.coroutines.channels.awaitClose

class RealtimeDB(val db: FirebaseDatabase) {

    companion object {
        private const val MESSAGES = "Messages"
        private const val PAGE_SIZE = 50
    }

    fun sendMessage(message: Message, uid: String) {
        db.getReference(MESSAGES).child(uid).push().setValue(message)
    }

    fun getMessage(uid: String, eventListener: ChildEventListener) = resultCatching {
        db.getReference(MESSAGES)
            .child(uid)
            .addChildEventListener(eventListener)
    }

    fun removeChildEventListenerForMessage(uid: String, eventListener: ChildEventListener) {
        db.getReference(MESSAGES).child(uid).removeEventListener(eventListener)
    }

    fun getMoreMessages(uid: String, messageList: List<Message>) {
        db.getReference(MESSAGES)
            .child(uid)
            .orderByChild("sentTime")
            .endAt(messageList[messageList.size - 1].sentTime.toString())
            .limitToLast(PAGE_SIZE)
            .get()
    }
}

object GetMessageFailException: Exception()
