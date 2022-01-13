package viewmodel

import com.example.binder.ui.usecase.ApproveFriendRequestsUseCase
import com.example.binder.ui.usecase.GetFriendRequestsUseCase
import repository.FirebaseRepository

class FriendRequestFragmentViewModel(
    private val getFriendRequestsUseCase: GetFriendRequestsUseCase,
    private val approveFriendRequestsUseCase: ApproveFriendRequestsUseCase<List<String>>
) : BaseViewModel() {

    private val marked = mutableSetOf<Int>()

    fun addMarkedIndex(index: Int) {
        marked.add(index)
    }
    fun removeMarkedIndex(index: Int){
        marked.remove(index)
    }

    fun getApproveFriendRequestResult() = approveFriendRequestsUseCase.getData()

    fun approveFriendRequests() {
        getFriendRequestsUseCase.getData().value?.let {
            it.data?.let { list ->
                approveFriendRequestsUseCase.setParameter(
                    list.filterIndexed { index, _ ->
                        index in marked 
                    }.mapNotNull { user -> user.uid }
                )
                clearSelected()
            }
        }
    }

    fun clearSelected() = marked.clear()

    fun getFriendRequests() = getFriendRequestsUseCase.getData()
}
