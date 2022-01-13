package viewmodel

import androidx.lifecycle.MutableLiveData

import data.Group
import Result
import androidx.lifecycle.Transformations
import com.example.binder.ui.usecase.CreateGroupUseCase
import com.example.binder.ui.usecase.GetFriendsUseCase
import data.User

class CreateGroupFragmentViewModel(private val createGroupUseCase: CreateGroupUseCase<Group>,
                                   private val getFriendsUseCase: GetFriendsUseCase) : BaseViewModel(){

    private val friends = MutableLiveData<Result<List<User>>>()

    private val members = mutableListOf<String>()

    private val marked = mutableSetOf<Int>()

    fun getMembers() = members

    fun addMarkedIndex(index: Int, uid: String) {
        marked.add(index)
        members.add(uid)
    }

    fun removeMarkedIndex(index: Int, uid: String) {
        marked.remove(index)
        members.remove(uid)
    }

    fun getFriends(): MutableLiveData<Result<List<User>>> {
        friends.postValue(getFriendsUseCase.getData().value)
        return friends
    }

    fun getFriendsStartingWith(name: String?): MutableLiveData<Result<List<User>>> {
        marked.clear()
        if (name.isNullOrEmpty()) {
            getFriends()
        } else {
            Transformations.map(getFriends()) { it ->
                it.data?.filter {
                    it.name!!.contains(name, true)
                }
            }
        }
        return friends
    }

    fun createGroup(name: String) {
        val group = Group(name, members)
        createGroupUseCase.setParameter(group)
        createGroupUseCase.getData()
    }
}
