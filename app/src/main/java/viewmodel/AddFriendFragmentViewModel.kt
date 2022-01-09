package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.User
import kotlinx.coroutines.launch
import Result
import data.FriendRequest
import repository.FirebaseRepository

class AddFriendFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {

    private val users = MutableLiveData<Result<List<User>>>()
    private val addFriends = MutableLiveData<Result<Void>>()

    private val marked = mutableSetOf<Int>()

    fun getUsers() = users
    fun getAddFriends() = addFriends

    fun sendUserFriendRequests(uid: String) {
        addFriends.value = Result.loading(null)
        viewModelScope.launch {
            users.value?.data?.let { users ->
                addFriends.postValue(
                    firebaseRepository.sendFriendRequests(
                        users.filterIndexed { index, _ -> index in marked }.map {
                            FriendRequest(uid, it.uid)
                        }
                    )
                )
            }
        }
    }

    fun addMarkedIndex(index: Int) {
        marked.add(index)
    }
    fun removeMarkedIndex(index: Int){
        marked.remove(index)
    }

    fun fetchUsersStartingWith(name: String) {
        marked.clear()
        viewModelScope.launch {
            users.postValue(firebaseRepository.searchUsersWithName(name))
        }
    }
}
