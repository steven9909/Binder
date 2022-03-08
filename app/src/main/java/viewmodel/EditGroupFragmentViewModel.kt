package viewmodel

import com.example.binder.ui.Item
import com.example.binder.ui.usecase.GetSpecificUserUseCase
import com.example.binder.ui.usecase.UpdateGroupNameUseCase
import com.example.binder.ui.viewholder.FriendDetailItem


class EditGroupFragmentViewModel(
    private val getSpecificUserUseCase: GetSpecificUserUseCase,
    private val updateGroupNameUseCase: UpdateGroupNameUseCase
) : BaseViewModel() {

    private val members = mutableListOf<Item>()

    fun getMembers() = members

    fun addMember(member: FriendDetailItem) {
        members.add(member)
    }

    fun removeMember(member: FriendDetailItem) {
        members.remove(member)
    }

    fun setSpecificUserInformation(uid: String) = getSpecificUserUseCase.setParameter(uid)

    fun getSpecificUserInformation() = getSpecificUserUseCase.getData()

    fun setUpdateGroupName(guid: String, name: String) = updateGroupNameUseCase.setParameter(Pair(guid, name))

    fun getUpdateGroupName() = updateGroupNameUseCase.getData()
}

