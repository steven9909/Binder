package viewmodel


import com.example.binder.ui.usecase.GetGroupsUseCase
import Result
import com.example.binder.ui.usecase.GetDMGroupAndUserUseCase
import com.example.binder.ui.usecase.RemoveFriendUseCase
import com.example.binder.ui.usecase.DeleteGroupUseCase
import combineWith
import data.DMGroup
import data.Group
import data.User


class FriendListFragmentViewModel(
    private val groupsUseCase: GetGroupsUseCase,
    private val getDMGroupAndUserUseCase: GetDMGroupAndUserUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase<String>,
    private val deleteGroupUseCase: DeleteGroupUseCase
) : BaseViewModel() {

    private fun getGroups() = groupsUseCase.getData()
    private fun getDMGroupAndUser() = getDMGroupAndUserUseCase.getData()

    fun <R> getGroupsAndUsers(
        block: (Result<List<Pair<User, DMGroup>>>?,
                Result<List<Group>>?) -> R
    ) = getDMGroupAndUser().combineWith(getGroups(), block)

    fun setRemoveFriendId(uid: String) {
        removeFriendUseCase.setParameter(uid)
    }

    fun setRemoveGroupId(guid: String) {
        deleteGroupUseCase.setParameter(guid)
    }

    fun getRemoveFriend() = removeFriendUseCase.getData()
}
