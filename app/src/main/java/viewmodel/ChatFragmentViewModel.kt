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
import com.example.binder.ui.usecase.GetQuestionFromDBUseCase
import com.example.binder.ui.usecase.GetVideoRoomUseCase
import com.example.binder.ui.usecase.GetVideoTokenUseCase
import com.example.binder.ui.usecase.SendMessageUseCase
import com.example.binder.ui.usecase.UploadFileToGoogleDriveUseCase
import data.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.GoogleDriveRepository
import java.io.File

@SuppressWarnings("TooManyFunctions")
class ChatFragmentViewModel(
    private val realtimeDB: RealtimeDB,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMoreMessagesUseCase: GetMoreMessagesUseCase,
    private val getQuestionFromDBUseCase: GetQuestionFromDBUseCase,
    private val getVideoTokenUseCase: GetVideoTokenUseCase<Pair<String, String>>,
    private val getVideoRoomUseCase: GetVideoRoomUseCase<Pair<String, String>>
): BaseViewModel(), KoinComponent {

    private val googleAccountProvider: GoogleAccountProvider by inject()

    private var googleDriveRepository: GoogleDriveRepository? = null

    private var createGoogleDriveFolderUseCase: CreateGoogleDriveFolderUseCase? = null
    private var uploadFileToGoogleDriveUseCase: UploadFileToGoogleDriveUseCase? = null

    fun initDrive(): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            googleDriveRepository = googleAccountProvider.tryGetDriveService()?.let {
                GoogleDriveRepository(it)
            }
            createGoogleDriveFolderUseCase = googleDriveRepository?.let {
                CreateGoogleDriveFolderUseCase(it)
            }
            uploadFileToGoogleDriveUseCase = googleDriveRepository?.let {
                UploadFileToGoogleDriveUseCase(it)
            }
        }
    }

    fun getMoreMessagesData() = getMoreMessagesUseCase.getData()

    fun getMoreMessages(uid: String, lastMessageTimestamp: Long) {
        val mapParam = Pair(uid, lastMessageTimestamp)
        getMoreMessagesUseCase.setParameter(mapParam)
    }

    fun getQuestionFromDBData() = getQuestionFromDBUseCase.getData()

    fun getQuestionFromDatabase(id: String) {
        getQuestionFromDBUseCase.setParameter(id)
    }

    fun setUploadFileParam(folderId: String, mimeType: String?, file: File) {
        uploadFileToGoogleDriveUseCase?.setParameter(Triple(folderId, mimeType, file))
    }

    fun getUploadFileData() = uploadFileToGoogleDriveUseCase?.getData()

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
                    if (ret?.get("question") != null) {
                        trySend(Message(
                            ret["sendingId"] as String,
                            ret["msg"] as String,
                            ret["timestamp"] as Long,
                            ret["fileLink"] as? String?,
                            Question(
                                ((ret["question"]) as HashMap<String, *>)["question"] as String,
                                ((ret["question"]) as HashMap<String, *>)["answers"] as List<String>,
                                ((ret["question"]) as HashMap<String, *>)["answerIndexes"] as List<Int>,
                                ((ret["question"]) as HashMap<String, *>)["questionType"] as String?),
                            ret["sentByName"] as? String?
                            )
                        )
                    } else {
                        trySend(Message(
                            ret?.get("sendingId") as String,
                            ret["msg"] as String,
                            ret["timestamp"] as Long,
                            ret["fileLink"] as? String?,
                            null,
                            ret["sentByName"] as? String?)
                        )
                    }
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

    fun setRoomIdAndUserId(roomId: String, uuid: String) {
        getVideoTokenUseCase.setParameter(Pair(roomId, uuid))
    }

    fun setGroupIdAndUserId(groupId: String, uuid: String) {
        getVideoRoomUseCase.setParameter(Pair(groupId, uuid))
    }

    fun getRoomId() = getVideoRoomUseCase.getData()

    fun getAuthToken() = getVideoTokenUseCase.getData()
    
}
