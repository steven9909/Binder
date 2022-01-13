package viewmodel

import com.example.binder.ui.usecase.GetFriendsUseCase
import com.example.binder.ui.usecase.GetGroupsUseCase
import repository.FirebaseRepository
import androidx.lifecycle.MutableLiveData
import Result
import androidx.lifecycle.viewModelScope
import data.DMGroup
import kotlinx.coroutines.launch

class FriendListFragmentViewModel(
    private val friendsUseCase: GetFriendsUseCase,
    private val groupsUseCase: GetGroupsUseCase,
    private val firebaseRepository: FirebaseRepository
) : BaseViewModel() {
    fun getFriends() = friendsUseCase.getData()
    fun getGroups() = groupsUseCase.getData()

    private val getDMGroupLD: MutableLiveData<Result<DMGroup>> = MutableLiveData<Result<DMGroup>>(Result.loading(null))

    fun getDMGroup(uid: String) {
        getDMGroupLD.value = Result.loading(null)
        viewModelScope.launch {
            getDMGroupLD.postValue(firebaseRepository.getUserDMGroup(uid))
        }
    }

    fun getDMGroupLiveData() = getDMGroupLD
}
