package viewmodel

import Result.Companion.loading
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.binder.ui.usecase.UpdateScheduleUseCase
import data.CalendarEvent
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class InputScheduleBottomSheetViewModel(val updateScheduleUseCase: UpdateScheduleUseCase,
                                        val firebaseRepository: FirebaseRepository
                                        ) : BaseViewModel() {

    fun updateSchedule(calendarEvent: CalendarEvent) {
        updateScheduleUseCase.setParameter(calendarEvent)
    }

    fun getSchedule() = updateScheduleUseCase.getData()

    fun deleteEvent(cid: String?) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.deleteUserCalendarEvent(cid))
    }
}
