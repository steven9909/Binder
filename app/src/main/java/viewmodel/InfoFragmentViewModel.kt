package viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import data.CalendarEvent
import data.Friends
import data.Settings
import data.User
import repository.FirebaseRepository
import repository.Result

class InfoFragmentViewModel(val firebaseRepository: FirebaseRepository) : ViewModel() {
    fun updateUserInformation(user: User): Result<Void> {
        return firebaseRepository.updateBasicUserInformation(user)
    }

    fun updateUserSettings(settings: Settings): Result<Void> {
        return firebaseRepository.updateGeneralUserSettings(settings)
    }

    fun updateUserFriends(friends: Friends): Result<Void> {
        return firebaseRepository.updateUserFriendList(friends)
    }

    fun updateSingleCalendarEvent(calendarEvent: CalendarEvent): Result<Void> {
        return firebaseRepository.updateUserCalendarEvent(calendarEvent)
    }

}