package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutHubFragmentBinding
import com.example.binder.databinding.LayoutVideoMenuFragmentBinding
import com.example.binder.databinding.LayoutVideoPlayerFragmentBinding
import data.CalendarConfig
import data.ChatConfig
//import data.HMSConfig
import data.HubConfig
import data.User
import data.VideoConfig
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.HMSConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.HubFragmentViewModel
import viewmodel.InfoFragmentViewModel
import viewmodel.MainActivityViewModel
import viewmodel.VideoMenuFragmentViewModel
import viewmodel.VideoPlayerFragmentViewModel

class VideoMenuFragment(override val config: VideoConfig) : BaseFragment() {
    override val viewModel: ViewModel by viewModel<VideoMenuFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val hmsSDK = HMSSDK.Builder(requireActivity().application).build()

    private var binding: LayoutVideoMenuFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutVideoMenuFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->
            binding.scheduleButton.setOnClickListener {
                val room_id = "61d914cc2779ba16a4e5ae29"
                val name = config.name
                val uuid = config.uid
                val authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2Nlc3Nfa2V5IjoiNjFkOGE0NzFiOGMzYzdiYzg2YTRkMzlhIiwicm9vbV9pZCI6IjYxZDkxNGNjMjc3OWJhMTZhNGU1YWUyOSIsInVzZXJfaWQiOiI2MWQ4YTQ3MWI4YzNjN2JjODZhNGQzOTYiLCJyb2xlIjoiaG9zdCIsImp0aSI6ImM0OTk0NjY3LTUzOTItNGM5Yi04M2M5LWVmZTI1MjkwYWU5OSIsInR5cGUiOiJhcHAiLCJ2ZXJzaW9uIjoyLCJleHAiOjE2NDE3MDMwMzN9.zi2DKzTpnlbQJLv-4WA-CMBXLVSr1aqDGKfq9-6oiW0"

                val hmsConfig = HMSConfig(name, authToken)

                joinRoom()
                fun joinRoom(config : HMSConfig, hmsUpdateListener : HMSUpdateListener){
                    hmsSDK.join(config, hmsUpdateListener)
                }

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
