package viewmodel

import com.example.binder.ui.usecase.ApproveFriendRequestsUseCase
import com.example.binder.ui.usecase.GetFriendRequestsUseCase
import repository.FirebaseRepository

class FriendRequestFragmentViewModel(
    private val getFriendRequestsUseCase: GetFriendRequestsUseCase,
    private val approveFriendRequestsUseCase: ApproveFriendRequestsUseCase
) : BaseViewModel() {

    private val marked = hashMapOf<String, String>()

    fun addMarkedIndex(uid: String, fruid: String) {
        marked[uid] = fruid
    }
    fun removeMarkedIndex(uid: String){
        marked.remove(uid)
    }

    fun getApproveFriendRequestResult() = approveFriendRequestsUseCase.getData()

    fun approveFriendRequests() {
        val list = marked.keys.map {
            Pair(it, marked[it])
        }
        getFriendRequestsUseCase.getData().value?.let {
            it.data?.let { list ->
                approveFriendRequestsUseCase.setParameter(
                    list
                )
                clearSelected()
            }
        }
    }

    fun clearSelected() = marked.clear()

    fun getFriendRequests() = getFriendRequestsUseCase.getData()
}
