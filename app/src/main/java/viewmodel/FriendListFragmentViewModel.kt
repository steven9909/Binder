package viewmodel

import com.example.binder.ui.usecase.GetFriendsUseCase
import com.example.binder.ui.usecase.GetGroupsUseCase
import repository.FirebaseRepository
import androidx.lifecycle.MutableLiveData
import Result
import androidx.lifecycle.viewModelScope
import com.example.binder.ui.usecase.GetDMGroupAndUserUseCase
import data.DMGroup
import kotlinx.coroutines.launch

class FriendListFragmentViewModel(
    private val friendsUseCase: GetFriendsUseCase,
    private val groupsUseCase: GetGroupsUseCase,
    private val getDMGroupAndUserUseCase: GetDMGroupAndUserUseCase
) : BaseViewModel() {
    fun getFriends() = friendsUseCase.getData()
    fun getGroups() = groupsUseCase.getData()
    fun getDMGroupAndUser() = getDMGroupAndUserUseCase.getData()

}
