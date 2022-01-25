package viewmodel

import com.example.binder.ui.usecase.DeleteGroupUseCase
import com.example.binder.ui.usecase.GetGroupsUseCase
import com.example.binder.ui.usecase.RemoveFriendUseCase
import com.example.binder.ui.usecase.RemoveGroupMemberUseCase
import data.Group

class FriendListFragmentViewModel(
    private val groupsUseCase: GetGroupsUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val removeGroupMemberUseCase: RemoveGroupMemberUseCase
) : BaseViewModel() {

    fun getGroups() = groupsUseCase.getData()

    fun setRemoveFriendId(uid: String, guid: String) {
        removeFriendUseCase.setParameter(Pair(uid, guid))
    }

    fun getRemoveFriend() = removeFriendUseCase.getData()

    fun setDeleteGroup(guid: String, members: List<String>) {
        deleteGroupUseCase.setParameter(Pair(guid, members))
    }

    fun getDeleteGroup() = deleteGroupUseCase.getData()

    fun setRemoveGroupMember(uid: String, guid: String) {
        removeGroupMemberUseCase.setParameter(Pair(uid, guid))
    }

    fun getRemoveGroupMember() = removeGroupMemberUseCase.getData()
}
