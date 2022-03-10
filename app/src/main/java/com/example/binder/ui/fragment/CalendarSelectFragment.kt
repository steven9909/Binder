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
import com.example.binder.ui.ClickType
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.FriendNameItem
import com.example.binder.ui.viewholder.HeaderItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.AddFriendConfig
import data.CalendarConfig
import data.CalendarSelectConfig
import data.ChatConfig
import data.CreateGroupConfig
import data.FriendRequestConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.CalendarSelectViewModel
import viewmodel.CreateGroupFragmentViewModel
import viewmodel.FriendListFragmentViewModel
import viewmodel.MainActivityViewModel

@Suppress("LongMethod")

class CalendarSelectFragment(override val config: CalendarSelectConfig): BaseFragment() {

    companion object {
        private const val FRIEND_HEADER = "friend"
        private const val GROUP_HEADER = "group"
        private const val VERTICAL_SPACING = 25
    }

    private var binding: LayoutCalendarSelectFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<CalendarSelectViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()

    override var items: MutableList<Item> = mutableListOf()

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(item: Item) {
            super.onViewSelected(item)
            (item as FriendNameItem)?.let {
                if (item.friendNameType == CalendarSelectFragment.FRIEND_HEADER) {
                    mainActivityViewModel.postNavigation(CalendarConfig(
                        item.name ?: "",
                        item.uid ?: "",
                        shouldOpenInStaticSheet = true
                    ))
                } else if (item.friendNameType == CalendarSelectFragment.GROUP_HEADER) {
                    mainActivityViewModel.postNavigation(CalendarConfig(
                        item.name ?: "",
                        item.guid ?: "",
                        item.owner == config.uid,
                        shouldOpenInStaticSheet = true
                    ))
                }
            }
        }
    }

    private lateinit var listAdapter: GenericListAdapter

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
                mainActivityViewModel.postNavigation(
                    CalendarConfig(
                        config.name,
                        config.uid,
                        shouldOpenInStaticSheet = true
                    )
                )
            }

            listAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.mainRecycler.layoutManager = LinearLayoutManager(context)
            binding.mainRecycler.adapter = listAdapter
            binding.mainRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )
            (viewModel as? CalendarSelectViewModel)?.setGroups()
            (viewModel as? CalendarSelectViewModel)?.getGroups()
                ?.observe(viewLifecycleOwner) { groups ->
                    val list = mutableListOf<Item>(
                        HeaderItem(
                            "0",
                            requireContext().getString(R.string.friend_list),
                            false,
                            false,
                            CalendarSelectFragment.FRIEND_HEADER
                        )
                    )
                    var isGroupHeaderAdded = false

                    if (groups.status == Status.SUCCESS && groups.data != null) {
                        groups.data.forEach { pair ->
                            val item = if (pair.first != null) {
                                val user = pair.first
                                FriendNameItem(
                                    user?.uid,
                                    user?.name,
                                    pair.second.uid,
                                    pair.second.owner,
                                    pair.second.members,
                                    null,
                                    CalendarSelectFragment.FRIEND_HEADER
                                )
                            } else {
                                if (!isGroupHeaderAdded) {
                                    isGroupHeaderAdded = true
                                    list.add(
                                        HeaderItem(
                                            "1",
                                            requireContext().getString(R.string.groups_list),
                                            false,
                                            false,
                                            CalendarSelectFragment.GROUP_HEADER
                                        )
                                    )
                                    FriendNameItem(
                                        null,
                                        pair.second.groupName,
                                        pair.second.uid,
                                        pair.second.owner,
                                        pair.second.members,
                                        pair.second.groupTypes,
                                        CalendarSelectFragment.GROUP_HEADER
                                    )
                                } else {
                                    FriendNameItem(
                                        null,
                                        pair.second.groupName,
                                        pair.second.uid,
                                        pair.second.owner,
                                        pair.second.members,
                                        pair.second.groupTypes,
                                        CalendarSelectFragment.GROUP_HEADER
                                    )
                                }
                            }
                            list.add(item)
                        }
                    }

                    if (!isGroupHeaderAdded) {
                        list.add(
                            HeaderItem(
                                "1",
                                requireContext().getString(R.string.groups_list),
                                false,
                                false,
                                CalendarSelectFragment.GROUP_HEADER
                            )
                        )
                        isGroupHeaderAdded = true
                    }
                    this.items.clear()
                    this.items.addAll(list)
                    listAdapter.submitList(this.items)
                }
        }
    }
}
