package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.User
import kotlinx.coroutines.launch
import Result
import repository.FirebaseRepository

class AddFriendFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {

    private val users = MutableLiveData<Result<List<User>>>()

    fun getUsers() = users

    fun fetchUsersStartingWith(name: String) {
        viewModelScope.launch {
            users.postValue(firebaseRepository.searchUsersWithName(name))
        }
    }
}