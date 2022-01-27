package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import data.Friend
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

@SuppressWarnings("EmptyClassBlock")
class FriendFinderFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel(){

}
