package viewmodel

import Result.Companion.loading
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class DayScheduleFragmentViewModel(val firebaseRepository: FirebaseRepository) : ViewModel() {

    fun getUserSchedule() = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.getUserCalendarEvents())
    }

}