package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import data.Friend
import data.Group
import kotlinx.coroutines.launch
import repository.FirebaseRepository

class CreateGroupFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel(){

    private var friends = MutableLiveData<Result<List<Friend>>>()

    private lateinit var members : List<String>

    private val marked = mutableSetOf<Int>()

    fun getFriends() {
        viewModelScope.launch {
            //TODO: fetch all friends info
        }
    }

    fun addMarkedIndex(index: Int) {
        marked.add(index)
    }

    fun removeMarkedIndex(index: Int) {
        marked.remove(index)
    }

    fun getFriendsStartingWith(name: String) {
        marked.clear()
        viewModelScope.launch {
            //TODO: filter friend list and fetch matching results
        }
    }

    fun createGroup(name: String) {
        val group = Group(name, members)
        viewModelScope.launch {
            firebaseRepository.createGroup(group)
        }
    }
}