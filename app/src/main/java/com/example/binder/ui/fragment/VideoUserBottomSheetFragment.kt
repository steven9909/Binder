package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binder.databinding.LayoutVideoUsersBottomSheetFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.FriendNameViewHolder
import com.example.binder.ui.viewholder.UserDataItem
import com.example.binder.ui.viewholder.VideoUserViewHolder
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.VideoUserBottomSheetConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import viewmodel.SharedVideoPlayerViewModel
import viewmodel.VideoUserBottomSheetViewModel

class VideoUserBottomSheetFragment(override val config: VideoUserBottomSheetConfig) : BaseBottomSheetFragment() {

    override val viewModel: ViewModel by viewModel<VideoUserBottomSheetViewModel>()

    private val sharedViewModel : SharedVideoPlayerViewModel by sharedViewModel()

    private var binding: LayoutVideoUsersBottomSheetFragmentBinding? = null


    private val viewHolderFactory: ViewHolderFactory by inject()

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(item: Item) {
            Timber.d("VideoPlayerFragment: OnSwiped : ${item.uid}")
            item.uid?.let { it1 -> sharedViewModel.setSharedData(it1) }
            dismiss()
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
    ): View {
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