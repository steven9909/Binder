package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import data.Friend
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class FriendFinderFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel(){

    //Set Functions

    //Get Functions
    fun getUserFriends() = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.getBasicUserFriends())
    }

    //Delete Functions
    fun deleteUserFriend(fuid: String) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.removeUserFriend(fuid))
    }
}
