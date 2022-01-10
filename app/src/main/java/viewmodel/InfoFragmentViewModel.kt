package viewmodel

import androidx.lifecycle.MutableLiveData
import data.User
import repository.FirebaseRepository
import Result
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class InfoFragmentViewModel(val firebaseRepository: FirebaseRepository) : BaseViewModel() {

    //Set Functions
    private val userLiveData: MutableLiveData<Result<Void>> = MutableLiveData<Result<Void>>(Result.loading(null))

    fun setUserInformation(user: User) {
        userLiveData.value = Result.loading(null)
        viewModelScope.launch {
            userLiveData.postValue(firebaseRepository.updateBasicUserInformation(user))
        }
    }

    fun getUserLiveData() = userLiveData

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
