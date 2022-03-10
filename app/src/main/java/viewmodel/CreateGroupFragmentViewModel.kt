package viewmodel

import data.Group
import com.example.binder.ui.usecase.CreateGroupUseCase
import com.example.binder.ui.usecase.GetFriendStartingWithUseCase

class CreateGroupFragmentViewModel(
    private val createGroupUseCase: CreateGroupUseCase,
    private val getFriendsStartingWithUseCase: GetFriendStartingWithUseCase,
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

    fun createGroup(name: String, uid: String, groupTypes: List<String>) {
        val group = Group(name, getMembers().toList().distinct(), uid, false, groupTypes)
        createGroupUseCase.setParameter(group)
    }

    fun getCreateGroup() = createGroupUseCase.getData()
}
