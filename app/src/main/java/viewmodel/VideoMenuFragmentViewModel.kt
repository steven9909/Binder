package viewmodel

import Result.Companion.loading
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.binder.ui.usecase.GetScheduleForUserUseCase
import com.example.binder.ui.usecase.GetVideoRoomUseCase
import com.example.binder.ui.usecase.GetVideoTokenUseCase
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository
import repository.TokenRepository

class VideoMenuFragmentViewModel(
    private val getVideoTokenUseCase: GetVideoTokenUseCase<Pair<String, String>>,
    private val getVideoRoomUseCase: GetVideoRoomUseCase<Pair<String, String>>,
    private val getScheduleForUserUseCase: GetScheduleForUserUseCase
    ) : BaseViewModel() {

    fun setRoomIdAndUserId(roomId: String, uuid: String) {
        getVideoTokenUseCase.setParameter(Pair(roomId, uuid))
    }

    fun setGroupIdAndUserId(groupId: String, uuid: String) {
        getVideoRoomUseCase.setParameter(Pair(groupId, uuid))
    }

    fun updateScheduleForUser(uid: String, startTime: Long, endTime: Long) {
        getScheduleForUserUseCase.setParameter(Triple(uid, startTime, endTime))
    }

    fun getScheduleForUser() = getScheduleForUserUseCase.getData()

    fun getRoomId() = getVideoRoomUseCase.getData()

    fun getAuthToken() = getVideoTokenUseCase.getData()

}
