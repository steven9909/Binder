package com.example.binder.ui.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutAddFriendFragmentBinding
import com.example.binder.databinding.LayoutFriendRequestFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.ListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.AddFriendConfig
import data.FriendRequestConfig
import data.HubConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.AddFriendFragmentViewModel
import viewmodel.FriendRequestFragmentViewModel
import viewmodel.MainActivityViewModel

class FriendRequestFragment (override val config: FriendRequestConfig) : BaseFragment() {

    companion object {
        private const val VERTICAL_SPACING = 25
    }

    private var binding: LayoutFriendRequestFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<FriendRequestFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var listAdapter: ListAdapter

    private val actionListener = object : OnActionListener {
        override fun onViewSelected(index: Int, clickInfo: ClickInfo?) {
            (viewModel as? FriendRequestFragmentViewModel)?.addMarkedIndex(index)
        }

        override fun onViewUnSelected(index: Int, clickInfo: ClickInfo?) {
            (viewModel as? FriendRequestFragmentViewModel)?.removeMarkedIndex(index)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFriendRequestFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->

            binding.titleText.text = SpannableStringBuilder().apply {
                val friendText = SpannableString(requireContext().getString(R.string.friend))
                friendText.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.app_yellow)),
                    0,
                    friendText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(friendText)
                this.append(" " + requireContext().getString(R.string.requests))
            }

            listAdapter = ListAdapter(viewHolderFactory, actionListener)

            binding.friendListRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendListRecycler.adapter = listAdapter
            binding.friendListRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(
                    VERTICAL_SPACING
                )
            )

            binding.sendRequestButton.setOnClickListener {
                (viewModel as? FriendRequestFragmentViewModel)?.approveFriendRequests()
            }

            (viewModel as? FriendRequestFragmentViewModel)?.getApproveFriendRequestResult()?.observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    mainActivityViewModel.postNavigation(HubConfig(config.name, config.uid, shouldBeAddedToBackstack = false))
                }
            }

            (viewModel as? FriendRequestFragmentViewModel)?.getFriendRequests()?.observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS && !it.data.isNullOrEmpty()) {
                    listAdapter.updateItems(it.data.map { user ->
                        FriendDetailItem(user.uid, user.name ?: "", user.school ?: "", user.program ?: "", user.interests ?: "")
                    })
                }
            }
        }
    }
}