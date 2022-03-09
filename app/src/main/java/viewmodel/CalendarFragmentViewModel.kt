package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import com.example.binder.ui.usecase.BatchCalendarEventUpdateUseCase
import com.example.binder.ui.usecase.GetScheduleForUserUseCase
import com.example.binder.ui.usecase.GetScheduleUseCase
import data.CalendarEvent
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class CalendarFragmentViewModel(
    private val batchCalendarEventUpdateUseCase: BatchCalendarEventUpdateUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getScheduleForUserUseCase: GetScheduleForUserUseCase
) : BaseViewModel() {

    fun updateSchedule(startTime: Long, endTime: Long) {
        getScheduleUseCase.setParameter(Pair(startTime, endTime))
    }

    fun updateScheduleForUser(uid:String, startTime: Long, endTime: Long) {
        getScheduleForUserUseCase.setParameter(Triple(uid, startTime, endTime))
    }

    fun getSchedule() = getScheduleUseCase.getData()

    fun getScheduleForUser() = getScheduleForUserUseCase.getData()

    fun setBatchCalendarEvents(uid: String, list: List<CalendarEvent>) {
        batchCalendarEventUpdateUseCase.setParameter(Pair(uid, list))
    }
    fun getBatchCalendarEvents() = batchCalendarEventUpdateUseCase.getData()
}
