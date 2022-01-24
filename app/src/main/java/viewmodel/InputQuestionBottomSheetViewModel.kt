package viewmodel

import com.example.binder.ui.usecase.AddQuestionToDBUseCase
import com.example.binder.ui.usecase.GetQuestionFromDBUseCase
import com.example.binder.ui.usecase.SendMessageUseCase
import data.Message
import data.Question

class InputQuestionBottomSheetViewModel(
    private val addQuestionToDBUseCase: AddQuestionToDBUseCase,
    private val getQuestionFromDBUseCase: GetQuestionFromDBUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : BaseViewModel() {

    fun getAddQuestionToDBData() = addQuestionToDBUseCase.getData()

    fun addQuestionToDatabase(question: Question) {
        addQuestionToDBUseCase.setParameter(question)
    }

    fun getMessageSendData() = sendMessageUseCase.getData()

    fun messageSend(message: Message, uid: String) {
        val mapParam = Pair(message, uid)
        sendMessageUseCase.setParameter(mapParam)
    }

    fun getQuestionFromDBData() = getQuestionFromDBUseCase.getData()

    fun getQuestionFromDatabase(id: String) {
        getQuestionFromDBUseCase.setParameter(id)
    }
}