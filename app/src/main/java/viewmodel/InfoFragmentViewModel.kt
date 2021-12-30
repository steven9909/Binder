package viewmodel

import androidx.lifecycle.ViewModel
import data.User
import repository.FirebaseRepository
import repository.Result

class InfoFragmentViewModel(val firebaseRepository: FirebaseRepository) : ViewModel() {
    fun updateUserInformation(user: User): Result<Void> {
        return firebaseRepository.updateBasicUserInformation(user)
    }
}