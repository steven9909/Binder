package viewmodel

import Result.Companion.loading
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.binder.ui.usecase.GetScheduleForUserUseCase
import com.example.binder.ui.usecase.GetScheduleUseCase
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.compose.getStateViewModel
import repository.FirebaseRepository

class DayScheduleFragmentViewModel(val getScheduleUseCase: GetScheduleUseCase, val getScheduleForUserUseCase: GetScheduleForUserUseCase) : BaseViewModel() {

    fun updateSchedule(startTime: Long, endTime: Long) {
        getScheduleUseCase.setParameter(Pair(startTime, endTime))
    }

    fun updateScheduleForUser(uid: String, startTime: Long, endTime: Long) {
        getScheduleForUserUseCase.setParameter(Triple(uid, startTime, endTime))
    }

    fun getSchedule() = getScheduleUseCase.getData()

    fun getScheduleForUser() = getScheduleForUserUseCase.getData()

}
