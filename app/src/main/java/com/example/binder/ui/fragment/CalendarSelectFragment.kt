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
import com.example.binder.databinding.LayoutCalendarFragmentBinding
import com.example.binder.databinding.LayoutCalendarSelectFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.FriendNameItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.CalendarConfig
import data.CalendarSelectConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.CalendarSelectViewModel
import viewmodel.CreateGroupFragmentViewModel
import viewmodel.MainActivityViewModel

class CalendarSelectFragment(override val config: CalendarSelectConfig): BaseFragment() {

    companion object{
        private const val VERTICAL_SPACING = 25
    }

    private var binding: LayoutCalendarSelectFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<CalendarSelectViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()
    private val groupViewHolderFactory: ViewHolderFactory by inject()

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(item: Item) {
            super.onViewSelected(item)
            (item as FriendDetailItem)?.let {
                item.uid?.let {

                    mainActivityViewModel.postNavigation(CalendarConfig(
                        item.name,
                        item.uid!!,
                        shouldOpenInStaticSheet = true
                    ))
                }
            }
        }
    }

    private val groupActionListener = object: OnActionListener {
        override fun onViewSelected(item: Item) {
            super.onViewSelected(item)
            (item as FriendNameItem)?.let {
                mainActivityViewModel.postNavigation(CalendarConfig(
                    item.name ?: "",
                    item.uid ?: "",
                    item.owner == config.uid,
                    shouldOpenInStaticSheet = true
                ))
            }
        }
    }

    private lateinit var listAdapter: GenericListAdapter
    private lateinit var groupListAdapter: GenericListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutCalendarSelectFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->

            binding.myCalendarButton.setOnClickListener {
                mainActivityViewModel.postNavigation(CalendarConfig(
                    config.name,
                    config.uid,
                    shouldOpenInStaticSheet = true
                ))
            }

            listAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.friendListRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendListRecycler.adapter = listAdapter
            binding.friendListRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )

            groupListAdapter = GenericListAdapter(groupViewHolderFactory, groupActionListener)
            binding.groupListRecycler.layoutManager = LinearLayoutManager(context)
            binding.groupListRecycler.adapter = groupListAdapter
            binding.groupListRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )

            (viewModel as CalendarSelectViewModel).getFriends().observe(viewLifecycleOwner) {
                when {
                    (it.status == Status.SUCCESS && it.data != null) -> {
                        binding.emptyView.visibility = View.GONE
                        listAdapter.submitList(it.data.map { friend ->
                            FriendDetailItem(
                                null,
                                friend.uid,
                                friend.name ?: "",
                                friend.school ?: "",
                                friend.program ?: "",
                                friend.interests?.joinToString(", ") { interest -> interest } ?: "",
                                ignoreClick = true
                            )
                        })
                    }
                    (it.status == Status.ERROR) -> {
                        listAdapter.submitList(null)
                        binding.emptyView.visibility = View.VISIBLE
                    }
                }
            }
            binding.searchButton.setOnClickListener {
                val name = binding.friendEdit.text.toString()
                (viewModel as CalendarSelectViewModel).getFriendsStartingWith(name)
            }

            (viewModel as CalendarSelectViewModel).getGroups().observe(viewLifecycleOwner) {
                when {
                    (it.status == Status.SUCCESS && it.data != null) -> {
                        groupListAdapter.submitList(it.data.map { pair ->
                            FriendNameItem(
                                null,
                                pair.second.groupName,
                                pair.second.uid,
                                pair.second.owner,
                                pair.second.members,
                                "group"
                            )
                        })
                    }
                }
            }

        }
    }
}
