package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutHubFragmentBinding
import data.AddFriendConfig
import data.CalendarSelectConfig
import data.FriendListConfig
import data.EditUserConfig
import data.HubConfig
import data.SettingsConfig
import data.VideoConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.HubFragmentViewModel
import viewmodel.MainActivityViewModel

class HubFragment(override val config: HubConfig) : BaseFragment() {
    override val viewModel: ViewModel by viewModel<HubFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private var binding: LayoutHubFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutHubFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }
    private fun setUpUi() {
        binding?.let { binding ->
            binding.meetingsButton.setOnClickListener {
                mainActivityViewModel.postNavigation(VideoConfig(config.name, config.uid))
            }
            binding.scheduleButton.setOnClickListener {
                mainActivityViewModel.postNavigation(CalendarSelectConfig(config.name, config.uid))
            }
            binding.messagesButton.setOnClickListener {
                mainActivityViewModel.postNavigation(AddFriendConfig(config.name, config.uid))
            }
            binding.socialButton.setOnClickListener {
                mainActivityViewModel.postNavigation(FriendListConfig(config.name, config.uid))
            }

            binding.nameText.text = config.name
            binding.nameText.setOnClickListener {
                mainActivityViewModel.postNavigation(EditUserConfig(config.name, config.uid))
            }

            binding.settingButton.setOnClickListener {
                mainActivityViewModel.postNavigation(SettingsConfig())
            }
        }
    }
}
