package viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import data.User
import repository.FirebaseRepository

class InfoFragmentViewModel(val firebaseRepository: FirebaseRepository) : ViewModel() {
    fun updateUserInformation(user: User): Task<Void> {
        return firebaseRepository.updateBasicUserInformation(user)
    }
}