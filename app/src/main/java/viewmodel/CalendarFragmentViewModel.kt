package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import data.CalendarEvent
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class CalendarFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {

    //Set Functions
    fun updateSingleCalendarEvent(calendarEvent: CalendarEvent) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.updateUserCalendarEvent(calendarEvent))
    }

    //Get Functions
    fun getCalendarEvents() = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.getUserCalendarEvents())
    }

}
