package viewmodel

import Result.Companion.loading
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class DayScheduleFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {

    fun getUserSchedule(startTimestamp: Timestamp, endTimestamp: Timestamp) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.getRelevantCalendarEvents(startTimestamp, endTimestamp))
    }

}
