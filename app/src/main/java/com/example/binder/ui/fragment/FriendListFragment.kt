package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutFriendListFragmentBinding
import com.example.binder.databinding.LayoutVideoMenuFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.ClickType
import com.example.binder.ui.Item
import com.example.binder.ui.ListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendNameItem
import com.example.binder.ui.viewholder.HeaderItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.AddFriendConfig
import data.Config
import data.FriendListConfig
import data.FriendRequestConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.AddFriendFragmentViewModel
import viewmodel.FriendListFragmentViewModel
import viewmodel.HubFragmentViewModel
import viewmodel.MainActivityViewModel

class FriendListFragment(override val config: FriendListConfig) : BaseFragment() {

    companion object{
        private const val VERTICAL_SPACING = 10
        private const val FRIEND_HEADER = "friend"
        private const val GROUP_HEADER = "group"
    }

    override val viewModel: ViewModel by viewModel<FriendListFragmentViewModel>()

    private var binding: LayoutFriendListFragmentBinding? = null

    private val viewHolderFactory: ViewHolderFactory by inject()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private lateinit var listAdapter: ListAdapter

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(index: Int, clickInfo: ClickInfo?) {
            when(clickInfo?.getSource()) {
                FRIEND_HEADER -> {
                    when(clickInfo.getType()) {
                        ClickType.ADD -> mainActivityViewModel.postNavigation(AddFriendConfig(config.name, config.uid))
                        ClickType.MESSAGE -> mainActivityViewModel.postNavigation(FriendRequestConfig(config.name, config.uid))
                    }
                }
                GROUP_HEADER -> {

                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFriendListFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->
            listAdapter = ListAdapter(viewHolderFactory, actionListener)

            binding.mainRecycler.layoutManager = LinearLayoutManager(context)
            binding.mainRecycler.adapter = listAdapter
            binding.mainRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )

            val sectionsToBeAdded = mutableListOf<Item>()

            sectionsToBeAdded.add(HeaderItem(requireContext().getString(R.string.friend_list), true, true, FRIEND_HEADER))
            (viewModel as? FriendListFragmentViewModel)?.getFriends()?.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        val list = it.data?.map { user ->
                            FriendNameItem(user.uid, user.name)
                        }
                        list?.let {
                            sectionsToBeAdded.addAll(list)
                        }
                    }
                }
            }
            sectionsToBeAdded.add(HeaderItem(requireContext().getString(R.string.groups_list), false, true, GROUP_HEADER))
            (viewModel as? FriendListFragmentViewModel)?.getGroups()?.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        val list = it.data?.map { user ->
                            FriendNameItem(group.uid, group.groupName)
                        }
                        list?.let {
                            sectionsToBeAdded.addAll(list)
                        }
                    }
                }
            }
            listAdapter.updateItems(sectionsToBeAdded)
        }
    }
}
