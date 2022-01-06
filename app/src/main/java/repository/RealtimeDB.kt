package repository

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import data.Message


class RealtimeDB(val fb: Firebase) {

    private val MESSAGES = "Messages"
    private val PAGESIZE = 50

    fun sendMessage(message: Message, uid: String) {
        fb.database.getReference(MESSAGES).child(uid).push().setValue(message)
    }

    fun getMessage(uid: String) {
        fb.database
            .getReference(MESSAGES)
            .child(uid)
            .orderByChild("sentTime")
            .limitToLast(PAGESIZE)
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val msg = snapshot.getValue(Message::class.java)

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    //Do Nothing
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    //Do Nothing
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    //Do Nothing
                }

                override fun onCancelled(error: DatabaseError) {
                    //Do Nothing
                }
        })
    }

    fun getMoreMessages(uid: String, messageList: List<Message>) {
        fb.database
            .getReference(MESSAGES)
            .child(uid)
            .orderByChild("sentTime")
            .endAt(messageList[messageList.size - 1].sentTime.toString())
            .limitToLast(PAGESIZE)
            .get()
    }
}

object GetMessageFailException: Exception()