package viewmodel

import androidx.lifecycle.ViewModel
import com.example.binder.ui.usecase.UpdateScheduleUseCase
import data.CalendarEvent

class InputScheduleBottomSheetViewModel(val updateScheduleUseCase: UpdateScheduleUseCase) : BaseViewModel() {

    fun updateSchedule(calendarEvent: CalendarEvent) {
        updateScheduleUseCase.setParameter(calendarEvent)
    }

    fun getSchedule() = updateScheduleUseCase.getData()
}
