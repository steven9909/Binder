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

class ChatFragmentViewModel(val realtimeDB: RealtimeDB) : BaseViewModel() {

    private fun messageGetterFlow(uid: String): Flow<String?> {
        return callbackFlow {
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    trySend(snapshot.value as? String)
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

    private val sendMessageLD: MutableLiveData<Result<Void>> = MutableLiveData<Result<Void>>(Result.loading(null))

    fun messageSender(message: Message, uid: String) {
        sendMessageLD.value = Result.loading(null)
        viewModelScope.launch {
            sendMessageLD.postValue(realtimeDB.sendMessage(message, uid))
        }
    }

    fun getUserSendMessageLiveData() = sendMessageLD

}
