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

            }

            listAdapter = ListAdapter(viewHolderFactory, actionListener)

            binding.friendListRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendListRecycler.adapter = listAdapter
            binding.friendListRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(
                    VERTICAL_SPACING
                )
            )
        }
    }
}