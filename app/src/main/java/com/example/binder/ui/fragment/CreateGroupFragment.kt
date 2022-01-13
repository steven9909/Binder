package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.databinding.LayoutCreateGroupBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.ListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.CreateGroupConfig
import data.HubConfig
import observeOnce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.AddFriendFragmentViewModel
import viewmodel.CreateGroupFragmentViewModel
import viewmodel.MainActivityViewModel

class CreateGroupFragment(override val config: CreateGroupConfig) : BaseFragment() {

    private var binding: LayoutCreateGroupBinding? = null

    override val viewModel: ViewModel by viewModel<CreateGroupFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var listAdapter: ListAdapter

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(index: Int, clickInfo: ClickInfo?) {
            if (clickInfo != null) {
                clickInfo.getSource()
                    ?.let { (viewModel as CreateGroupFragmentViewModel).addMarkedIndex(index, it) }
            }
        }
        override fun onViewUnSelected(index: Int, clickInfo: ClickInfo?) {
            if (clickInfo != null) {
                clickInfo.getSource()
                    ?.let { (viewModel as CreateGroupFragmentViewModel).removeMarkedIndex(index, it) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutCreateGroupBinding.inflate(inflater, container, false)

        setUpUi()
        return binding!!.root
    }

    private fun setUpUi(){
        binding?.let { binding ->

            listAdapter = ListAdapter(viewHolderFactory, actionListener)
            binding.friendListRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendListRecycler.adapter = listAdapter

            (viewModel as CreateGroupFragmentViewModel).getFriends().observe(viewLifecycleOwner) {
                when {
                    (it.status == Status.SUCCESS && it.data != null) -> {
                        listAdapter.updateItems(it.data.map { friend ->
                            FriendDetailItem(
                                friend.uid,
                                friend.name ?: "",
                                friend.school ?: "",
                                friend.program ?: "",
                                friend.interests ?: ""
                            )
                        })
                    }
                }
            }

            binding.searchButton.setOnClickListener {
                val name = binding.friendEdit.text.toString()
                (viewModel as CreateGroupFragmentViewModel).getFriendsStartingWith(name).observe(viewLifecycleOwner) {
                    when {
                        (it.status == Status.SUCCESS && it.data != null) -> {
                            listAdapter.updateItems(it.data.map { friend ->
                                FriendDetailItem(
                                    friend.uid,
                                    friend.name ?: "",
                                    friend.school ?: "",
                                    friend.program ?: "",
                                    friend.interests ?: ""
                                )
                            })
                        }
                    }
                }
            }

            binding.sendRequestButton.setOnClickListener {
                val name = binding.groupEdit.text.toString()
                (viewModel as CreateGroupFragmentViewModel).createGroup(name)
                (viewModel as AddFriendFragmentViewModel).getAddFriends().observeOnce(this) {
                    when {
                        (it.status == Status.SUCCESS) ->
                            mainActivityViewModel.postNavigation(
                                HubConfig(config.name, config.uid, shouldBeAddedToBackstack = false)
                            )
                    }
                }
            }
        }
    }
}