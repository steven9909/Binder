package viewmodel

import com.example.binder.ui.usecase.ApproveFriendRequestsUseCase
import com.example.binder.ui.usecase.GetFriendRequestsUseCase

class FriendRequestFragmentViewModel(
    private val getFriendRequestsUseCase: GetFriendRequestsUseCase,
    private val approveFriendRequestsUseCase: ApproveFriendRequestsUseCase
) : BaseViewModel() {

    private val marked = hashMapOf<String?, String?>()

    fun addMarkedIndex(uid: String?, fruid: String?) {
        marked[uid] = fruid
    }
    fun removeMarkedIndex(uid: String?){
        marked.remove(uid)
    }

    fun getApproveFriendRequestResult() = approveFriendRequestsUseCase.getData()

    fun approveFriendRequests() {
        approveFriendRequestsUseCase.setParameter(
            marked.keys.mapNotNull { key ->
                key?.let {
                    marked[key]?.let { fruid ->
                        Pair(key, fruid)
                    }
                }
            }
        )
        clearSelected()
    }

    fun clearSelected() = marked.clear()

    fun getFriendRequests() = getFriendRequestsUseCase.getData()
}
