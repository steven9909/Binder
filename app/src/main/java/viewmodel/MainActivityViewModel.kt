package viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.binder.ui.BaseFragment
import com.example.binder.ui.EmptyFragment
import com.example.binder.ui.FriendFinderFragment
import com.example.binder.ui.InfoFragment
import com.example.binder.ui.LoginFragment
import com.example.binder.ui.*
import data.Config
import data.FriendFinderConfig
import data.HubConfig
import data.InfoConfig
import data.LoginConfig

class MainActivityViewModel : ViewModel(){

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

    fun mappedFragmentLiveData() = Transformations.switchMap(navigationLiveData) {
        val fragment = when (it) {
            is LoginConfig -> LoginFragment(it)
            is InfoConfig -> InfoFragment(it)
            is HubConfig -> HubFragment(it)
            is FriendFinderConfig -> FriendFinderFragment(it)
            else -> EmptyFragment(it)
        }

        val liveData = MutableLiveData<FragmentCarrier>()
        liveData.value = FragmentCarrier(fragment, it.shouldBeAddedToBackstack)
        return@switchMap liveData
    }

}

data class FragmentCarrier (val fragment: Fragment, val shouldBeAddedToBackStack: Boolean) {
}