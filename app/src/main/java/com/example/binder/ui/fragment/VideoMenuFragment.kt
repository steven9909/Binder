package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutVideoMenuFragmentBinding
import data.VideoConfig
import data.VideoPlayerConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.MainActivityViewModel
import viewmodel.VideoMenuFragmentViewModel

class VideoMenuFragment(override val config: VideoConfig) : BaseFragment() {
    override val viewModel: ViewModel by viewModel<VideoMenuFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private var binding: LayoutVideoMenuFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutVideoMenuFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->
            binding.scheduleButton.setOnClickListener {
                mainActivityViewModel.postNavigation(VideoPlayerConfig(config.name, config.uid))
            }
        }
//        binding?.let { binding ->
//            binding.scheduleButton.setOnClickListener {
//                mainActivityViewModel.postNavigation(CalendarConfig())
//            }
//            binding.messagesButton.setOnClickListener {
//                mainActivityViewModel.postNavigation(ChatConfig())
//            }
//        }
    }
}
