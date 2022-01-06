package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import data.Friends
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class FriendFinderFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel(){

    //Set Functions
    fun updateUserFriends(friendId: String, friendName: String) = liveData(Dispatchers.IO){
        emit(loading(data = null))
        emit(firebaseRepository.updateUserFriendList(friendId, friendName))
    }

    //Get Functions
    fun getUserFriends() = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.getBasicUserFriends())
    }
}
