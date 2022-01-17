package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binder.R
import com.example.binder.databinding.LayoutFriendListFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.ClickType
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.BaseViewHolder
import com.example.binder.ui.viewholder.FriendNameItem
import com.example.binder.ui.viewholder.FriendNameViewHolder
import com.example.binder.ui.viewholder.HeaderItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.AddFriendConfig
import data.ChatConfig
import data.DMGroup
import data.FriendListConfig
import data.FriendRequestConfig
import observeOnce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.FriendListFragmentViewModel
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

    private lateinit var genericListAdapter: GenericListAdapter

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(index: Int, clickInfo: ClickInfo?) {
            when(clickInfo?.getSource()) {
                FRIEND_HEADER -> {
                    when(clickInfo.getType()) {
                        ClickType.ADD ->
                            mainActivityViewModel.postNavigation(AddFriendConfig(config.name, config.uid))
                        ClickType.MESSAGE ->
                            mainActivityViewModel.postNavigation(FriendRequestConfig(config.name, config.uid))
                    }
                }
                GROUP_HEADER -> {

                }
                else -> {
                    if (clickInfo != null) {
                        mainActivityViewModel.postNavigation(ChatConfig(
                            config.name,
                            config.uid,
                            clickInfo.getSource() as String,
                            "Messaging"))
                    }
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

    @SuppressWarnings("NestedBlockDepth")
    private fun setUpUi() {
        binding?.let { binding ->
            genericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)

            binding.mainRecycler.layoutManager = LinearLayoutManager(context)
            binding.mainRecycler.adapter = genericListAdapter
            binding.mainRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )

            val simpleCallBack = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    return when (viewHolder) {
                        is FriendNameViewHolder -> super.getSwipeDirs(recyclerView, viewHolder)
                        else -> 0
                    }
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    (genericListAdapter.getItemAt(viewHolder.bindingAdapterPosition) as? FriendNameItem)?.let {
                        when (it.friendNameType) {
                            FRIEND_HEADER -> {
                                it.uid?.let { uid ->
                                    (viewModel as? FriendListFragmentViewModel)?.setRemoveFriendId(
                                        uid
                                    )
                                    val list = ArrayList(genericListAdapter.currentList)
                                    list.removeAt(viewHolder.bindingAdapterPosition)
                                    genericListAdapter.submitList(list)
                                }
                            }
                            GROUP_HEADER -> {

                            }
                            else -> Unit
                        }
                    }
                }
            }
            ItemTouchHelper(simpleCallBack).attachToRecyclerView(binding.mainRecycler)

            (viewModel as? FriendListFragmentViewModel)?.getRemoveFriend()?.observe(viewLifecycleOwner) {

            }

            (viewModel as? FriendListFragmentViewModel)?.getGroups()?.observe(viewLifecycleOwner) { groups ->
                val list = mutableListOf<Item>(HeaderItem("0",
                    requireContext().getString(R.string.friend_list),
                    true,
                    true,
                    FRIEND_HEADER))
                list.addAll(
                    if (groups.status == Status.SUCCESS && groups.data != null) {
                        groups.data.map { pair ->
                            if (pair.first != null) {
                                FriendNameItem(
                                    null,
                                    pair.first.name,
                                    pair.second.uid,
                                    FRIEND_HEADER
                                )
                            } else {
                                FriendNameItem(
                                    pair.first.uid,
                                    pair.first.name,
                                    pair.second.uid,
                                    FRIEND_HEADER
                                )
                            }
                        }
                    } else {
                        emptyList()
                    }
                )

                list.add(HeaderItem("1",
                    requireContext().getString(R.string.groups_list),
                    false,
                    true,
                    GROUP_HEADER
                ))
                list.addAll(
                    if (groups?.status == Status.SUCCESS && groups.data != null) {
                        groups.data.map { group ->
                            FriendNameItem(group.uid, group.groupName, group.uid, GROUP_HEADER)
                        }
                    } else {
                        emptyList()
                    }
                )

                genericListAdapter.submitList(list)

            }?.observe(viewLifecycleOwner) {
                Unit
            }
        }
    }
}
