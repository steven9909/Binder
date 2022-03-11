package viewmodel;

import com.example.binder.ui.usecase.GetSpecificUserUseCase
import com.example.binder.ui.usecase.RemoveFriendUseCase

class FriendProfileFragmentViewModel (
    private val getSpecificUserUseCase: GetSpecificUserUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase,
    ) : BaseViewModel() {

        fun setRemoveFriendId(uid: String, guid: String) {
            removeFriendUseCase.setParameter(Pair(uid, guid))
        }

        fun getRemoveFriend() = removeFriendUseCase.getData()

        fun setSpecificUserInformation(uid: String) {
            getSpecificUserUseCase.setParameter(uid)
        }

        fun getSpecificUserInformation() = getSpecificUserUseCase.getData()
}
