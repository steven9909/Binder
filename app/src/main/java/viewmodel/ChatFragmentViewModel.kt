package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import data.Message
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import repository.RealtimeDB
import Result
import com.example.binder.ui.usecase.SendMessageUseCase

class ChatFragmentViewModel(private val realtimeDB: RealtimeDB, private val sendMessageUseCase: SendMessageUseCase<Pair<Message, String>>) : BaseViewModel() {

    private val getMoreMessageLD: MutableLiveData<Result<List<Message>>> = MutableLiveData<Result<List<Message>>>(Result.loading(null))

    fun getMoreMessages(uid: String, lastMessageTimestamp: Long) {
        getMoreMessageLD.value = Result.loading(null)
        viewModelScope.launch {
            getMoreMessageLD.postValue(realtimeDB.getMoreMessages(uid, lastMessageTimestamp))
        }
    }

    fun getMoreMessagesLiveData() = getMoreMessageLD

    fun getMessageSendData() = sendMessageUseCase.getData()

    fun messageSend(message: Message, uid: String) {
        val mapParam = Pair(message, uid)
        sendMessageUseCase.setParameter(mapParam)
    }

    fun messageGetterFlow(uid: String): Flow<Pair<Any?, Any?>?> {
        return callbackFlow {
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val ret = snapshot.value as? Map<String, Any>
                    val pair = Pair(ret?.get("sendingId"), ret?.get("msg"))
                    trySend(pair)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Unit
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Unit
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Unit
                }

                override fun onCancelled(error: DatabaseError) {
                    cancel()
                }
            }
            realtimeDB.getMessage(uid, childEventListener)
            awaitClose {
                realtimeDB.removeChildEventListenerForMessage(uid, childEventListener)
            }
        }
    }
}
