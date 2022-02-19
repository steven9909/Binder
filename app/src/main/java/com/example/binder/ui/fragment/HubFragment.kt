package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.R
import com.example.binder.databinding.LayoutHubFragmentBinding
import data.AddFriendConfig
import data.CalendarConfig
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
import java.text.SimpleDateFormat

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
            binding.settingsButton.setOnClickListener {
                mainActivityViewModel.postNavigation(SettingsConfig())
            }
            binding.nameText.text = config.name
            binding.scheduleLayout.setOnClickListener {
                mainActivityViewModel.postNavigation(CalendarConfig(config.name, config.uid, shouldOpenInStaticSheet = true))
                binding.fragmentName.text = requireContext().getString(R.string.calendar)
            }
            binding.meetingsLayout.setOnClickListener {
                mainActivityViewModel.postNavigation(VideoConfig(config.name, config.uid, shouldOpenInStaticSheet = true))
                binding.fragmentName.text = requireContext().getString(R.string.meetings)
            }
            binding.exploreLayout.setOnClickListener {
                binding.fragmentName.text = requireContext().getString(R.string.explore)
            }
            binding.groupLayout.setOnClickListener {
                mainActivityViewModel.postNavigation(FriendListConfig(config.name, config.uid, shouldOpenInStaticSheet = true))
                binding.fragmentName.text = requireContext().getString(R.string.group)
            }
        }
    }
}
