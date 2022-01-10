package viewmodel

import androidx.lifecycle.MutableLiveData
import data.User
import repository.FirebaseRepository

class FriendListFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel()
