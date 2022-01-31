package viewmodel

import com.example.binder.ui.usecase.GetUserInformationUseCase
import com.example.binder.ui.usecase.UpdateUserInformationUserCase
import data.User

class EditUserFragmentViewModel(
    private val updateUserInformationUserCase: UpdateUserInformationUserCase,
    private val getUserInformationUseCase: GetUserInformationUseCase
) : BaseViewModel() {

    fun getUserInformation() = getUserInformationUseCase.getData()

    fun setUpdateUserInformation(user: User) {
        updateUserInformationUserCase.setParameter(user)
    }

    fun getUpdateUserInformation() = updateUserInformationUserCase.getData()
}
