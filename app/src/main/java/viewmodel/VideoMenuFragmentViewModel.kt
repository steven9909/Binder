package viewmodel

import Result.Companion.loading
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class VideoMenuFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {
    fun getUserFriends() = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.getBasicUserFriends())
    }
}
