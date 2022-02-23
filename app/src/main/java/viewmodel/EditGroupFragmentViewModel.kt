package viewmodel;

import com.example.binder.ui.usecase.GetGroupInformationUseCase
import com.example.binder.ui.usecase.UpdateGroupInformationUseCase
import data.Group

class EditGroupFragmentViewModel(
    private val updateGroupInformationUserCase: UpdateGroupInformationUseCase,
    private val getGroupInformationUseCase: GetGroupInformationUseCase
) : BaseViewModel() {

    fun getGroupInformation() = getGroupInformationUseCase.getData()

    fun setUpdateGroupInformation(group: Group) {
        updateGroupInformationUserCase.setParameter(group)
    }

    fun getUpdateGroupInformation() = updateGroupInformationUserCase.getData()
}

