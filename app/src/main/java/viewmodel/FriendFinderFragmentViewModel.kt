package viewmodel

import Result.Companion.loading
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import data.Friends
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class FriendFinderFragmentViewModel(val firebaseRepository: FirebaseRepository) : ViewModel(){

    //Set Functions
    fun updateUserFriends(friends: Friends) = liveData(Dispatchers.IO){
        emit(loading(data = null))
        emit(firebaseRepository.updateUserFriendList(friends))
    }

    //Get Functions

}