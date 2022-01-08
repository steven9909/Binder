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

    fun getFriendFUIDForDelete(friendId: String) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.getFriendFUID(friendId))
    }

    //Delete Functions
    /**
     * @param fuid: UID of friend record to be deleted in FriendList for current user
     * @param friendId: UID of friend
     * @param friendFUID: UID from getFriendFUIDForDelete() function
     */
    fun deleteUserFriend(fuid: String, friendId: String, friendFUID: String) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.removeUserFriend(fuid, friendId, friendFUID))
    }
}
