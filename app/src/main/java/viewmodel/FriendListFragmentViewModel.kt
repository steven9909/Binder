package viewmodel

import com.example.binder.ui.usecase.GetFriendsUseCase
import com.example.binder.ui.usecase.GetGroupsUseCase

class FriendListFragmentViewModel(
    private val friendsUseCase: GetFriendsUseCase,
    private val groupsUseCase: GetGroupsUseCase
) : BaseViewModel() {
    fun getFriends() = friendsUseCase.getData()
    fun getGroups() = groupsUseCase.getData()
}
