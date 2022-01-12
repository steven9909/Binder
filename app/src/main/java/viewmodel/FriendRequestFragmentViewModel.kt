package viewmodel

import com.example.binder.ui.usecase.GetFriendRequestsUseCase

class FriendRequestFragmentViewModel(private val getFriendRequestsUseCase: GetFriendRequestsUseCase) : BaseViewModel() {

    private val marked = mutableSetOf<Int>()

    fun addMarkedIndex(index: Int) {
        marked.add(index)
    }
    fun removeMarkedIndex(index: Int){
        marked.remove(index)
    }

    fun getFriendRequests() = getFriendRequestsUseCase.getData()
}