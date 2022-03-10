package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import com.example.binder.ui.usecase.DeleteScheduleUseCase
import com.example.binder.ui.usecase.UpdateScheduleUseCase
import data.CalendarEvent
import kotlinx.coroutines.Dispatchers

class VideoScheduleBottomSheetViewModel (val updateScheduleUseCase: UpdateScheduleUseCase,
                                         val deleteScheduleUseCase: DeleteScheduleUseCase
) : BaseViewModel() {

    fun updateSchedule(calendarEvent: CalendarEvent) {
        updateScheduleUseCase.setParameter(calendarEvent)
    }

    fun getSchedule() = updateScheduleUseCase.getData()

    fun deleteEvent(uid: String, cid: String) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(deleteScheduleUseCase.setParameter(Pair(uid, cid)))
    }

    fun getScheduleToDelete() = deleteScheduleUseCase.getData()

}
