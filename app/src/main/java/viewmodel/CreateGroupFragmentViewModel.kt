package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import data.Friend
import data.Group
import Result
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.usecase.CreateGroupUseCase
import kotlinx.coroutines.launch
import repository.FirebaseRepository

class CreateGroupFragmentViewModel(private val createGroupUseCase: CreateGroupUseCase<Group>) : BaseViewModel(){

    private val friends = MutableLiveData<Result<List<Friend>>>()

    private val members = mutableListOf<String>()

    private val marked = mutableSetOf<Int>()

    fun getFriends() = friends

    fun getMembers() = members

    fun addMarkedIndex(index: Int, uid: String) {
        marked.add(index)
        members.add(uid)
    }

    fun removeMarkedIndex(index: Int, uid: String) {
        marked.remove(index)
        members.remove(uid)
    }

    fun getFriendsStartingWith(name: String?): MutableLiveData<Result<List<Friend>>> {
        marked.clear()
        if (name == null) {
            //TODO: fetch all friends
        } else {
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