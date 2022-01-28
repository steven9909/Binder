package viewmodel

import com.example.binder.ui.usecase.AddQuestionToDBUseCase
import com.example.binder.ui.usecase.GetGroupTypesUseCase
import com.example.binder.ui.usecase.GetQuestionFromDBUseCase
import com.example.binder.ui.usecase.SendMessageUseCase
import data.Message
import data.Question

class InputQuestionBottomSheetViewModel(
    private val addQuestionToDBUseCase: AddQuestionToDBUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getGroupTypesUseCase: GetGroupTypesUseCase
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

    fun getGroupTypesData() = getGroupTypesUseCase.getData()

    fun getGroupTypes(guid: String) {
        getGroupTypesUseCase.setParameter(guid)
    }
}
