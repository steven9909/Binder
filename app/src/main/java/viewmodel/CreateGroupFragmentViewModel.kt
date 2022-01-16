package viewmodel

import androidx.lifecycle.MutableLiveData

import data.Group
import Result
import com.example.binder.ui.usecase.CreateGroupUseCase
import com.example.binder.ui.usecase.GetFriendStartingWithUseCase
import com.example.binder.ui.usecase.GetFriendsUseCase
import data.User

class CreateGroupFragmentViewModel(private val createGroupUseCase: CreateGroupUseCase<Group>,
                                   private val getFriendsUseCase: GetFriendsUseCase,
                                   private val getFriendsStartingWithUseCase: GetFriendStartingWithUseCase<String>
                                   ) : BaseViewModel(){

    private val members = mutableSetOf<String>()

    private fun getMembers() = members

    fun addMember(uid: String) {
        members.add(uid)
    }

    fun removeMember(uid: String) {
        members.remove(uid)
    }

    fun getFriendsStartingWith(name: String) {
        getFriendsStartingWithUseCase.setParameter(name)
    }

    fun getFriends() = getFriendsStartingWithUseCase.getData()

    fun createGroup(name: String) {
        val group = Group(name, getMembers().toList())
        createGroupUseCase.setParameter(group)
    }

    fun getCreateGroup() = createGroupUseCase.getData()
}
