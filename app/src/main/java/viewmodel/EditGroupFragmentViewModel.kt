package viewmodel

import com.example.binder.ui.usecase.GetSpecificUserUseCase
import com.example.binder.ui.usecase.RemoveGroupMemberUseCase
import com.example.binder.ui.usecase.UpdateGroupNameUseCase
import com.example.binder.ui.viewholder.FriendDetailItem


class EditGroupFragmentViewModel(
    private val getSpecificUserUseCase: GetSpecificUserUseCase,
    private val updateGroupNameUseCase: UpdateGroupNameUseCase,
    private val removeGroupMemberUseCase: RemoveGroupMemberUseCase
) : BaseViewModel() {

    private val members = mutableListOf<FriendDetailItem>()

    private val removed = mutableListOf<String>()

    fun getMembers() = members

    fun getRemoved() = removed

    fun addMember(member: FriendDetailItem) {
        members.add(member)
    }

    fun removeMember(member: FriendDetailItem) {
        members.remove(member)
    }

    fun addRemoved(uid: String) {
        removed.add(uid)
    }

    fun removeRemoved(uid: String) {
        removed.remove(uid)
    }

    fun setSpecificUserInformation(uid: String) {
        getSpecificUserUseCase.setParameter(uid)
    }

    fun getSpecificUserInformation() = getSpecificUserUseCase.getData()

    fun setUpdateGroupName(guid: String, name: String) {
        updateGroupNameUseCase.setParameter(Pair(guid, name))
    }

    fun getUpdateGroupName() = updateGroupNameUseCase.getData()

    fun setRemoveGroupMember(guid: String) {
        for (uid in removed) {
            removeGroupMemberUseCase.setParameter(Pair(uid, guid))
        }
    }


    fun getRemoveGroupMember() = removeGroupMemberUseCase.getData()
}

