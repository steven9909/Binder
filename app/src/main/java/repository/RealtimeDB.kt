package repository

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import data.Message
import resultCatching


class RealtimeDB(val fb: Firebase) {

    private val MESSAGES = "Messages"

    fun sendMessage(message: Message, groupID: String) {
        fb.database.getReference(MESSAGES).child(groupID).push().setValue(message)
    }

    fun getMessages(groupID: String) {
        fb.database.getReference(MESSAGES).child(groupID).get().addOnSuccessListener {
            if (it.exists()) {
                val messages = listOf(it.getValue(Message::class.java))
            } else {
                throw GetMessageFailException
            }
        }.addOnFailureListener {
            throw GetMessageFailException
        }
    }
}

object GetMessageFailException: Exception()