package viewmodel


import Result.Companion.loading
import androidx.lifecycle.liveData
import data.User
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class InfoFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {

    //Set Functions
    fun updateUserInformation(user: User) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.updateBasicUserInformation(user))
    }

    //Get Functions

}
