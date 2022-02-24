package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutVideoPlayerFragmentBinding
import com.example.binder.databinding.LayoutVideoUsersBottomSheetFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.UserDataItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.VideoPlayerConfig
import data.VideoUserBottomSheetConfig
import live.hms.video.sdk.HMSSDK
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.MainActivityViewModel
import viewmodel.ScheduleDisplayBottomSheetViewModel
import viewmodel.SharedVideoPlayerViewModel
import viewmodel.VideoPlayerFragmentViewModel
import viewmodel.VideoUserBottomSheetViewModel

class VideoUserBottomSheetFragment(override val config: VideoUserBottomSheetConfig) : BaseBottomSheetFragment() {

    override val viewModel: ViewModel by viewModel<VideoUserBottomSheetViewModel>()

    private val sharedViewModel : ViewModel by sharedViewModel<SharedVideoPlayerViewModel>()

    private var binding: LayoutVideoUsersBottomSheetFragmentBinding? = null


    private val viewHolderFactory: ViewHolderFactory by inject()

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(index: Int, clickInfo: ClickInfo?) {
            Unit
        }

        override fun onViewUnSelected(index: Int, clickInfo: ClickInfo?) {
            Unit
        }
    }

    private lateinit var genericListAdapter: GenericListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutVideoUsersBottomSheetFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }
    private fun setUpUi() {
        binding?.let { binding ->

            genericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)

            binding.userNameList.layoutManager = LinearLayoutManager(context)
            binding.userNameList.adapter = genericListAdapter

            genericListAdapter.submitList(config.people)

        }
    }
}