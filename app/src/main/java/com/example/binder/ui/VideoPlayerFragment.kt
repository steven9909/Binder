package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutHubFragmentBinding
import com.example.binder.databinding.LayoutVideoPlayerFragmentBinding
import data.CalendarConfig
import data.HubConfig
import data.VideoConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.HubFragmentViewModel
import viewmodel.MainActivityViewModel
import viewmodel.VideoPlayerFragmentViewModel

class VideoPlayerFragment(override val config: VideoConfig) : BaseFragment() {
    override val viewModel: ViewModel by viewModel<VideoPlayerFragmentViewModel>()

    private var binding: LayoutVideoPlayerFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutVideoPlayerFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}
