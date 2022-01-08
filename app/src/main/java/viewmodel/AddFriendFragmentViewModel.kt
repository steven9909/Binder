package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.User
import kotlinx.coroutines.launch
import Result
import data.Friend
import repository.FirebaseRepository

class AddFriendFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {

    private val users = MutableLiveData<Result<List<User>>>()
    private val addFriends = MutableLiveData<Result<Unit>>()

    private val marked = mutableSetOf<Int>()

    fun getUsers() = users
    fun getAddFriends() = addFriends

    fun addFriends(name: String, uid: String) {
        addFriends.value = Result.loading(null)
        viewModelScope.launch {
            users.value?.data?.let { users ->
                addFriends.postValue(firebaseRepository.addFriends(users.filterIndexed
                { index, _ -> index in marked }.map {
                    Friend(it.userId, name, uid)
                }))
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
