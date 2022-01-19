package viewmodel

import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import data.Message
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import repository.RealtimeDB
import com.example.binder.ui.GoogleAccountProvider
import com.example.binder.ui.usecase.CreateGoogleDriveFolderUseCase
import com.example.binder.ui.usecase.GetMoreMessagesUseCase
import com.example.binder.ui.usecase.SendMessageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.GoogleDriveRepository

class ChatFragmentViewModel(
    private val realtimeDB: RealtimeDB,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMoreMessagesUseCase: GetMoreMessagesUseCase
): BaseViewModel(), KoinComponent {

    private val googleAccountProvider: GoogleAccountProvider by inject()

    private var googleDriveRepository: GoogleDriveRepository? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            googleDriveRepository = googleAccountProvider.tryGetDriveService()?.let {
                GoogleDriveRepository(it)
            }
        }
    }

    private val createGoogleDriveFolderUseCase by lazy {
        googleDriveRepository?.let {
            CreateGoogleDriveFolderUseCase(it)
        }
    }

    fun getMoreMessagesData() = getMoreMessagesUseCase.getData()

    fun getMoreMessages(uid: String, lastMessageTimestamp: Long) {
        val mapParam = Pair(uid, lastMessageTimestamp)
        getMoreMessagesUseCase.setParameter(mapParam)
    }

    fun getMessageSendData() = sendMessageUseCase.getData()

    fun messageSend(message: Message, uid: String) {
        val mapParam = Pair(message, uid)
        sendMessageUseCase.setParameter(mapParam)
    }

    fun tryCreateFolder(guid: String) {
        createGoogleDriveFolderUseCase?.setParameter(guid)
    }

    fun getCreateFolderData() = createGoogleDriveFolderUseCase?.getData()

    fun messageGetterFlow(uid: String): Flow<Message> {
        return callbackFlow {
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val ret = snapshot.value as? Map<String, Any>
                    trySend(Message(
                        ret?.get("sendingId") as String,
                        ret["msg"] as String,
                        ret["timestamp"] as Long,
                        ret["read"] as Boolean)
                    )
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
