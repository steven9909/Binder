package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.User
import kotlinx.coroutines.launch
import Result
import com.example.binder.ui.usecase.GetFriendRequestsUseCase
import repository.FirebaseRepository

class AddFriendFragmentViewModel(val firebaseRepository: FirebaseRepository, private val getFriendRequestsUseCase: GetFriendRequestsUseCase) : BaseViewModel() {

    private val users = MutableLiveData<Result<List<User>>>()
    private val addFriends = MutableLiveData<Result<Void>>()

    private val marked = hashSetOf<String>()

    fun addMarkedIndex(uid: String?) {
        if (uid != null) {
            marked.add(uid)
        }
    }
    fun removeMarkedIndex(uid: String?){
        marked.remove(uid)
    }

    fun getUsers() = users
    fun getAddFriends() = addFriends

    fun sendUserFriendRequests(uid: String) {
        addFriends.value = Result.loading(null)
        viewModelScope.launch {
            users.value?.data?.let { users ->
                addFriends.postValue(
                    firebaseRepository.sendFriendRequests(
                        marked.toList()
                    )
                )
            }
        }
    }

    fun fetchUsersStartingWith(name: String) {
        marked.clear()
        viewModelScope.launch {
            users.postValue(firebaseRepository.searchNonFriendUsersWithName(name))
        }
    }
}
