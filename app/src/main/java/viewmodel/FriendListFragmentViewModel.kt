package viewmodel

import com.example.binder.ui.usecase.GetFriendsUseCase
import com.example.binder.ui.usecase.GetGroupsUseCase
import repository.FirebaseRepository
import androidx.lifecycle.MutableLiveData
import Result
import androidx.lifecycle.viewModelScope
import com.example.binder.ui.usecase.GetDMGroupAndUserUseCase
import com.example.binder.ui.usecase.RemoveFriendUseCase
import combineWith
import data.DMGroup
import data.Group
import data.User
import kotlinx.coroutines.launch

class FriendListFragmentViewModel(
    private val groupsUseCase: GetGroupsUseCase,
    private val getDMGroupAndUserUseCase: GetDMGroupAndUserUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase<String>
) : BaseViewModel() {
    private fun getGroups() = groupsUseCase.getData()
    private fun getDMGroupAndUser() = getDMGroupAndUserUseCase.getData()
    fun <R> getGroupsAndUsers(block: (Result<List<Pair<User, DMGroup>>>?, Result<List<Group>>?) -> R) = getDMGroupAndUser().combineWith(getGroups(), block)
    fun setRemoveFriendId(uid: String) {
        removeFriendUseCase.setParameter(uid)
    }
    fun getRemoveFriend() = removeFriendUseCase.getData()
}
