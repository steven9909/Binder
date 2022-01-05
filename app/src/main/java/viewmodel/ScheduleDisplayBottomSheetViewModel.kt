package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class ScheduleDisplayBottomSheetViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {
    fun deleteEvent(cid: String?) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.deleteUserCalendarEvent(cid))
    }
}