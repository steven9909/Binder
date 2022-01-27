package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import com.example.binder.ui.usecase.BatchCalendarEventUpdateUseCase
import com.example.binder.ui.usecase.GetScheduleUseCase
import data.CalendarEvent
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class CalendarFragmentViewModel(
    private val batchCalendarEventUpdateUseCase: BatchCalendarEventUpdateUseCase,
    private val getScheduleUseCase: GetScheduleUseCase
) : BaseViewModel() {

    fun updateSchedule(startTime: Long, endTime: Long) {
        getScheduleUseCase.setParameter(Pair(startTime, endTime))
    }

    fun getSchedule() = getScheduleUseCase.getData()

    fun setBatchCalendarEvents(list: List<CalendarEvent>) {
        batchCalendarEventUpdateUseCase.setParameter(list)
    }
    fun getBatchCalendarEvents() = batchCalendarEventUpdateUseCase.getData()
}
