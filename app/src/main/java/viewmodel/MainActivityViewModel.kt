package viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.binder.ui.*
import data.*


class MainActivityViewModel : BaseViewModel(){

    private val navigationLiveData: MutableLiveData<Config> = MutableLiveData()
    private val loadingLivedata: MutableLiveData<Boolean> = MutableLiveData()

    fun getNavigationLiveData() = navigationLiveData

    fun getLoadingLiveData() = loadingLivedata

    fun postNavigation(config: Config) {
        navigationLiveData.postValue(config)
    }

    fun postLoadingScreenState(isLoading: Boolean) {
        loadingLivedata.postValue(isLoading)
    }

    fun mappedFragmentLiveData() = Transformations.map(navigationLiveData) {
        val fragment = when (it) {
            is LoginConfig -> LoginFragment(it)
            is InfoConfig -> InfoFragment(it)
            is HubConfig -> HubFragment(it)
            is FriendFinderConfig -> FriendFinderFragment(it)
            is CalendarConfig -> CalendarFragment(it)
            is InputScheduleBottomSheetConfig -> InputScheduleBottomSheetFragment(it)
            else -> EmptyFragment(it)
        }

        FragmentCarrier(fragment, it.shouldBeAddedToBackstack, it is BottomSheetConfig)
    }

}

data class FragmentCarrier (val fragment: Fragment, val shouldBeAddedToBackStack: Boolean, val isBottomSheet: Boolean)
