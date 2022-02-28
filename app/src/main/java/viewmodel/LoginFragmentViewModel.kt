package viewmodel

import androidx.lifecycle.ViewModel
import com.example.binder.ui.usecase.DoesUserExistUseCase

class LoginFragmentViewModel(private val doesUserExistUseCase: DoesUserExistUseCase) : BaseViewModel() {
    fun doesUserExist() = doesUserExistUseCase.getData()
}
