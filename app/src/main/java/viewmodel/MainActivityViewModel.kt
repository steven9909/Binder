package viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.binder.ui.CalendarFragment
import com.example.binder.ui.ChatFragment
import com.example.binder.ui.EmptyFragment
import com.example.binder.ui.FriendFinderFragment
import com.example.binder.ui.HubFragment
import com.example.binder.ui.InfoFragment
import com.example.binder.ui.InputScheduleBottomSheetFragment
import com.example.binder.ui.LoginFragment
import com.example.binder.ui.VideoMenuFragment
import com.example.binder.ui.VideoPlayerFragment
import data.BottomSheetConfig
import data.CalendarConfig
import data.ChatConfig
import data.Config
import data.FriendFinderConfig
import data.HubConfig
import data.InfoConfig
import data.InputScheduleBottomSheetConfig
import data.LoginConfig
import data.VideoConfig
import data.VideoPlayerConfig


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
            is ChatConfig -> ChatFragment(it)
            is VideoConfig -> VideoMenuFragment(it)
            is VideoPlayerConfig -> VideoPlayerFragment(it)
            else -> EmptyFragment(it)
        }

        FragmentCarrier(fragment, it.shouldBeAddedToBackstack, it is BottomSheetConfig)
    }

}

data class FragmentCarrier (val fragment: Fragment, val shouldBeAddedToBackStack: Boolean, val isBottomSheet: Boolean)
