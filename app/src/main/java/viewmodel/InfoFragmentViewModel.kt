package viewmodel

import androidx.lifecycle.ViewModel
import data.User
import repository.FirebaseRepository
import toLiveData

class InfoFragmentViewModel(val firebaseRepository: FirebaseRepository) : ViewModel() {
    fun updateUserInformation(user: User) {
        val task = firebaseRepository.updateBasicUserInformation(user)
        return task.toLiveData()
    }
}