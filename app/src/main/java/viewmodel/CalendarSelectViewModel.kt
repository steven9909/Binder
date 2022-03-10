package viewmodel

import com.example.binder.ui.usecase.GetFriendStartingWithUseCase
import com.example.binder.ui.usecase.GetGroupsUseCase

class CalendarSelectViewModel(
    private val getGroupsUseCase: GetGroupsUseCase
    ): BaseViewModel() {

    fun getGroups() = getGroupsUseCase.getData()

}
