package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import data.Friend
import data.Group
import Result
import com.example.binder.ui.usecase.CreateGroupUseCase
import kotlinx.coroutines.launch
import repository.FirebaseRepository

class CreateGroupFragmentViewModel(private val createGroupUseCase: CreateGroupUseCase<Group>) : BaseViewModel(){

    private val friends = MutableLiveData<Result<List<Friend>>>()

    private lateinit var members : List<String>

    private val marked = mutableSetOf<Int>()

    fun getFriends() = friends

    fun getMemebers() = members

    fun addMarkedIndex(index: Int) {
        marked.add(index)
    }

    fun removeMarkedIndex(index: Int) {
        marked.remove(index)
    }

    fun getFriendsStartingWith(name: String): MutableLiveData<Result<List<Friend>>> {
        marked.clear()
        viewModelScope.launch {
            //TODO: Create useCase for searching friend using names and call it
        }
        return friends
    }

    fun createGroup(name: String) {
        val group = Group(name, members)
        createGroupUseCase.setParameter(group)
        createGroupUseCase.getData()
    }
}