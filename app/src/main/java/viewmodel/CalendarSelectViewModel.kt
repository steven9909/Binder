package viewmodel

import com.example.binder.ui.usecase.GetFriendStartingWithUseCase
import com.example.binder.ui.usecase.GetGroupsUseCase

class CalendarSelectViewModel(
    private val getFriendsStartingWithUseCase: GetFriendStartingWithUseCase,
    private val getGroupsUseCase: GetGroupsUseCase
    ): BaseViewModel() {

    fun getFriendsStartingWith(name: String) {
        getFriendsStartingWithUseCase.setParameter(name)
    }

    fun getFriends() = getFriendsStartingWithUseCase.getData()

    fun getGroups() = getGroupsUseCase.getData()

}
