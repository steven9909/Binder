package viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.binder.ui.EmptyFragment
import com.example.binder.ui.InfoFragment
import com.example.binder.ui.LoginFragment
import data.Config
import data.InfoConfig
import data.LoginConfig

class MainActivityViewModel : ViewModel(){

    private val navigationLiveData: MutableLiveData<Config> = MutableLiveData()

    fun getNavigationLiveData() = navigationLiveData

    fun postNavigation(config: Config) {
        navigationLiveData.postValue(config)
    }

    fun mappedFragmentLiveData() = Transformations.switchMap(navigationLiveData) {
        val fragment = when (it) {
            is LoginConfig -> LoginFragment()
            is InfoConfig -> InfoFragment()
            else -> EmptyFragment()
        }

        val liveData = MutableLiveData<Fragment>()
        liveData.value = fragment
        return@switchMap liveData
    }

}