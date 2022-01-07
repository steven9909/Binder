package repository

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import data.Message

@Suppress("UnusedPrivateMember")
class RealtimeDB(val db: FirebaseDatabase) {

    companion object {
        private const val MESSAGES = "Messages"
        private const val PAGE_SIZE = 50
    }

    fun sendMessage(message: Message, uid: String) {
        db.getReference(MESSAGES).child(uid).push().setValue(message)
    }

    fun getMessage(uid: String) {
        db.getReference(MESSAGES)
            .child(uid)
            .orderByChild("sentTime")
            .limitToLast(PAGE_SIZE)
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
        db.getReference(MESSAGES)
            .child(uid)
            .orderByChild("sentTime")
            .endAt(messageList[messageList.size - 1].sentTime.toString())
            .limitToLast(PAGE_SIZE)
            .get()
    }
}

object GetMessageFailException: Exception()