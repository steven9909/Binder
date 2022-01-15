package viewmodel

import androidx.lifecycle.MutableLiveData

import data.Group
import Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.binder.ui.usecase.CreateGroupUseCase
import com.example.binder.ui.usecase.GetFriendStartingWithUseCase
import com.example.binder.ui.usecase.GetFriendsUseCase
import data.User

class CreateGroupFragmentViewModel(private val createGroupUseCase: CreateGroupUseCase<Group>,
                                   private val getFriendsUseCase: GetFriendsUseCase,
                                   private val getFriendsStartingWithUseCase: GetFriendStartingWithUseCase<String>
                                   ) : BaseViewModel(){

    private val friends = MutableLiveData<Result<List<User>>>()

    private val members = mutableSetOf<String>()

    fun getMembers() = members

    fun addMarkedIndex(uid: String) {
        members.add(uid)
    }

    fun removeMarkedIndex(uid: String) {
        members.remove(uid)
    }

    fun getFriends(): MutableLiveData<Result<List<User>>> {
        friends.postValue(getFriendsUseCase.getData().value)
        return friends
    }

    fun getFriendsStartingWith(name: String?): MutableLiveData<Result<List<User>>> {
        if (name.isNullOrEmpty()) {
            getFriends()
        } else {
            friends.postValue(getFriendsStartingWithUseCase.getData().value)
        }
        return friends
    }

    fun createGroup(name: String): LiveData<Result<Void>> {
        val group = Group(name, members.toList())
        createGroupUseCase.setParameter(group)
        return createGroupUseCase.getData()
    }
}
