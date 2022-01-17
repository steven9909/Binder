package viewmodel

import com.example.binder.ui.usecase.GetGroupsUseCase
import com.example.binder.ui.usecase.RemoveFriendUseCase

class FriendListFragmentViewModel(
    private val groupsUseCase: GetGroupsUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase<String>
) : BaseViewModel() {

    fun getGroups() = groupsUseCase.getData()

    fun setRemoveFriendId(uid: String) {
        removeFriendUseCase.setParameter(uid)
    }

    fun getRemoveFriend() = removeFriendUseCase.getData()
}
