package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import data.Settings
import data.User
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class EditUserFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {

    //Set Functions
    fun updateUserInformation(user: User) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.updateBasicUserInformation(user))
    }

    fun updateUserSettings(settings: Settings) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.updateGeneralUserSettings(settings))
    }

    //Get Functions
    fun getUserInformation() = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.getBasicUserInformation())
    }

    fun getUserSettings() = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.getBasicUserSettings())
    }
}
