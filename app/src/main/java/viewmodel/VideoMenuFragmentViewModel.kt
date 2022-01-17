package viewmodel

import Result.Companion.loading
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.binder.ui.usecase.GetVideoTokenUseCase
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository
import repository.TokenRepository

class VideoMenuFragmentViewModel(private val getVideoTokenUseCase: GetVideoTokenUseCase<Pair<String, String>>) : BaseViewModel() {

    fun setRoomIdAndUserId(roomId: String, uuid: String) {
        getVideoTokenUseCase.setParameter(Pair(roomId, uuid))
    }

    fun getRoomIdAndUserId() = getVideoTokenUseCase.getData()

}
