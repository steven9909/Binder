package viewmodel

import androidx.lifecycle.MutableLiveData

class SharedVideoPlayerViewModel : BaseViewModel() {
    private val sharedData = MutableLiveData<String>()

    fun setSharedData(data: String) {
        sharedData.postValue(data)
    }

    fun getSharedData() = sharedData
}