package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import data.CalendarEvent
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class CalendarFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel()
