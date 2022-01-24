package viewmodel

import com.example.binder.ui.usecase.AddQuestionToDBUseCase
import com.example.binder.ui.usecase.GetQuestionFromDBUseCase
import data.Question

class InputQuestionBottomSheetViewModel(
    private val addQuestionToDBUseCase: AddQuestionToDBUseCase,
    private val getQuestionFromDBUseCase: GetQuestionFromDBUseCase
) : BaseViewModel() {

    fun getAddQuestionToDBData() = addQuestionToDBUseCase.getData()

    fun addQuestionToDatabase(question: Question) {
        addQuestionToDBUseCase.setParameter(question)
    }

    fun getQuestionFromDBData() = getQuestionFromDBUseCase.getData()

    fun getQuestionFromDatabase(id: String) {
        getQuestionFromDBUseCase.setParameter(id)
    }
}