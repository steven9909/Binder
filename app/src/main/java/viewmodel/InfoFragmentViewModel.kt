package viewmodel


import Result.Companion.loading
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
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

    fun updateUserSettings(settings: Settings) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.updateGeneralUserSettings(settings))
    }
    
    /**
    @TODO update

    fun updateUserFriends(friends: Friends): Result<Void> {
        return firebaseRepository.updateUserFriendList(friends)
    }

    fun updateSingleCalendarEvent(calendarEvent: CalendarEvent): Result<Void> {
        return firebaseRepository.updateUserCalendarEvent(calendarEvent)
    }

    //Get Functions
    fun getUserInformation(): MutableLiveData<User> {
        return firebaseRepository.getBasicUserInformation()
    }
    */
}