package viewmodel


import Result.Companion.loading
import androidx.lifecycle.liveData
import data.Group
import data.User
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class InfoFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {

    //Set Functions
    fun updateUserInformation(user: User) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.updateBasicUserInformation(user))
    }

    //Get Functions

    /**
     * @TODO move these to Group View Model
    fun createNewGroup(member: Group) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.createGroup(member))
    }

    fun addNewMemberToGroup(guid:String, member: String) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.addGroupMember(guid, member))
    }

    fun getAllCurrentUserGroups() = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.getAllUserGroups())
    }

    fun removeUserFromGroup(guid:String, member: String) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        emit(firebaseRepository.deleteGroupMember(guid, member))
    }
    */
}
