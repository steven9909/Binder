package viewmodel


import Result.Companion.loading
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import data.CalendarEvent
import data.Friends
import data.Settings
import data.User
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class InfoFragmentViewModel(val firebaseRepository: FirebaseRepository) : ViewModel() {

    //Set Functions
    fun updateUserInformation(user: User) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.updateBasicUserInformation(user))
    }

    //Get Functions

}