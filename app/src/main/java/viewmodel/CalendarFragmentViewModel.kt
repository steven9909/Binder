package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import com.example.binder.ui.usecase.BatchCalendarEventUpdateUseCase
import data.CalendarEvent
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class CalendarFragmentViewModel(val batchCalendarEventUpdateUseCase: BatchCalendarEventUpdateUseCase) : BaseViewModel() {
    fun setBatchCalendarEvents(list: List<CalendarEvent>) {
        batchCalendarEventUpdateUseCase.setParameter(list)
    }
    fun getBatchCalendarEvents() = batchCalendarEventUpdateUseCase.getData()
}
