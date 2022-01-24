package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import com.example.binder.ui.usecase.GetScheduleUseCase
import data.CalendarEvent
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class CalendarFragmentViewModel(val getScheduleUseCase: GetScheduleUseCase) : BaseViewModel() {

    fun updateSchedule(startTime: Long, endTime: Long) {
        getScheduleUseCase.setParameter(Pair(startTime, endTime))
    }

    fun getSchedule() = getScheduleUseCase.getData()
}
