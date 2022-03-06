package viewmodel

import com.example.binder.ui.usecase.FriendRecommendationUseCase
import com.example.binder.ui.usecase.SendFriendRequestsUseCase


class FriendRecommendationFragmentViewModel(
    private val friendRecommendationUseCase: FriendRecommendationUseCase,
    private val sendFriendRequestsUseCase: SendFriendRequestsUseCase
) : BaseViewModel() {

    private val marked = mutableSetOf<String>()

    fun addMarkedIndex(userId: String?) {
        if (userId != null) {
            marked.add(userId)
        }
    }
    fun removeMarkedIndex(userId: String?){
        marked.remove(userId)
    }

    fun setRecommendationParam(userId: String) {
        friendRecommendationUseCase.setParameter(userId)
    }

    fun getRecommendation() = friendRecommendationUseCase.getData()

    fun setFriendRequestParam() {
        sendFriendRequestsUseCase.setParameter(marked.toList())
        marked.clear()
    }

    fun getFriendRequest() = sendFriendRequestsUseCase.getData()
}
