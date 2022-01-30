package viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.binder.ui.fragment.ScheduleDisplayBottomSheetFragment
import com.example.binder.ui.fragment.VideoMenuFragment
import com.example.binder.ui.fragment.VideoPlayerFragment
import com.example.binder.ui.fragment.AddFriendFragment
import com.example.binder.ui.fragment.CalendarFragment
import com.example.binder.ui.fragment.ChatFragment
import com.example.binder.ui.fragment.DayScheduleFragment
import com.example.binder.ui.fragment.CreateGroupFragment
import com.example.binder.ui.fragment.EditUserFragment
import com.example.binder.ui.fragment.EmptyFragment
import com.example.binder.ui.fragment.FriendFinderFragment
import com.example.binder.ui.fragment.FriendListFragment
import com.example.binder.ui.fragment.FriendRecommendationFragment
import com.example.binder.ui.fragment.FriendRequestFragment
import com.example.binder.ui.fragment.HubFragment
import com.example.binder.ui.fragment.InfoFragment
import com.example.binder.ui.fragment.InputQuestionBottomSheetFragment
import com.example.binder.ui.fragment.InputScheduleBottomSheetFragment
import com.example.binder.ui.fragment.LoginFragment
import com.example.binder.ui.usecase.UpdateMessagingTokenUseCase
import data.AddFriendConfig
import data.BottomSheetConfig
import data.CalendarConfig
import data.ChatConfig
import data.Config
import data.DayScheduleConfig
import data.CreateGroupConfig
import data.EditUserConfig
import data.FriendFinderConfig
import data.FriendListConfig
import data.FriendRecommendationConfig
import data.FriendRequestConfig
import data.HubConfig
import data.InfoConfig
import data.InputQuestionBottomSheetConfig
import data.InputScheduleBottomSheetConfig
import data.LoginConfig
import data.VideoConfig
import data.VideoPlayerConfig
import data.ScheduleDisplayBottomSheetConfig


class MainActivityViewModel(private val updateMessagingTokenUseCase: UpdateMessagingTokenUseCase) : BaseViewModel(){

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

    @SuppressWarnings("ComplexMethod")
    fun mappedFragmentLiveData() = Transformations.map(navigationLiveData) {
        val fragment = when (it) {
            is LoginConfig -> LoginFragment(it)
            is InfoConfig -> InfoFragment(it)
            is HubConfig -> HubFragment(it)
            is FriendFinderConfig -> FriendFinderFragment(it)
            is CalendarConfig -> CalendarFragment(it)
            is InputScheduleBottomSheetConfig -> InputScheduleBottomSheetFragment(it)
            is ScheduleDisplayBottomSheetConfig -> ScheduleDisplayBottomSheetFragment(it)
            is ChatConfig -> ChatFragment(it)
            is VideoConfig -> VideoMenuFragment(it)
            is VideoPlayerConfig -> VideoPlayerFragment(it)
            is AddFriendConfig -> AddFriendFragment(it)
            is FriendListConfig -> FriendListFragment(it)
            is FriendRequestConfig -> FriendRequestFragment(it)
            is CreateGroupConfig -> CreateGroupFragment(it)
            is FriendRecommendationConfig -> FriendRecommendationFragment(it)
            is InputQuestionBottomSheetConfig -> InputQuestionBottomSheetFragment(it)
            is EditUserConfig -> EditUserFragment(it)
            is DayScheduleConfig -> DayScheduleFragment(it)
            else -> EmptyFragment(it)
        }

        FragmentCarrier(fragment, it.shouldBeAddedToBackstack, it is BottomSheetConfig)
    }

    fun getCloudMessagingToken() = updateMessagingTokenUseCase.getData()

}

data class FragmentCarrier (val fragment: Fragment, val shouldBeAddedToBackStack: Boolean, val isBottomSheet: Boolean)
