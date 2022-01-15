package viewmodel

import androidx.lifecycle.MutableLiveData

import data.Group
import Result
import androidx.lifecycle.LiveData
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

    fun getFriends() = friends

    fun getMembers() = members

    fun addMarkedIndex(uid: String) {
        members.add(uid)
    }

    fun removeMarkedIndex(uid: String) {
        members.remove(uid)
    }

    fun getFriendsStartingWith(name: String?) {
        if (name.isNullOrEmpty()) {
                getFriendsUseCase.getData().value?.let {
                    friends.postValue(it)
            }
        } else {
                getFriendsStartingWithUseCase.setParameter(name)
                getFriendsStartingWithUseCase.getData().value?.let {
                    friends.postValue(it)
                }
        }
    }

    fun createGroup(name: String): LiveData<Result<Void>> {
        val group = Group(name, members.toList())
        createGroupUseCase.setParameter(group)
        return createGroupUseCase.getData()
    }
}
