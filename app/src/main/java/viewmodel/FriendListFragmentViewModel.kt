package viewmodel

import com.example.binder.ui.usecase.GetGroupsUseCase
import com.example.binder.ui.usecase.RemoveFriendUseCase

class FriendListFragmentViewModel(
    private val groupsUseCase: GetGroupsUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase
) : BaseViewModel() {

    fun getGroups() = groupsUseCase.getData()

    fun setRemoveFriendId(uid: String, guid: String) {
        removeFriendUseCase.setParameter(Pair(uid, guid))
    }

    fun getRemoveFriend() = removeFriendUseCase.getData()
}
