package com.example.binder.ui.fragment

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.binder.ui.viewholder.FriendNameItem
import com.example.binder.ui.viewholder.FriendNameViewHolder
import com.example.binder.ui.viewholder.HeaderItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.AddFriendConfig
import data.ChatConfig
import data.CreateGroupConfig
import data.FriendListConfig
import data.FriendRequestConfig
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

    override var items: MutableList<Item> = mutableListOf()

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
                    when(clickInfo.getType()) {
                        ClickType.ADD ->
                            mainActivityViewModel.postNavigation(
                                CreateGroupConfig(
                                    config.name,
                                    config.uid
                                )
                            )
                        else -> Unit
                    }
                }
                else -> {
                    if (clickInfo != null) {
                        mainActivityViewModel.postNavigation(ChatConfig(
                            config.name,
                            config.uid,
                            clickInfo.getSource() as String,
                            clickInfo.getName() as String,
                            clickInfo.getOwner() as String,
                            clickInfo.getMembers() as List<String>,
                            clickInfo.getGroupType() as List<String>?))
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutFriendListFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    @SuppressWarnings("NestedBlockDepth", "LongMethod")
    private fun setUpUi() {
        binding?.let { binding ->
            genericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)

            binding.mainRecycler.layoutManager = LinearLayoutManager(context)
            binding.mainRecycler.adapter = genericListAdapter
            binding.mainRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )

            binding.swipeRefreshLayout.setOnRefreshListener {
                (viewModel as? FriendListFragmentViewModel)?.refreshGroups()
            }

            val simpleCallBack = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val p = Paint()
                    p.color = requireContext().getColor(R.color.app_red)
                    c.drawRect(itemView.right.toFloat() + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(),  itemView.bottom.toFloat(), p)
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
                                    it.guid?.let { guid ->
                                        (viewModel as? FriendListFragmentViewModel)?.setRemoveFriendId(
                                            uid,
                                            guid
                                        )
                                        items.removeAt(viewHolder.bindingAdapterPosition)
                                        genericListAdapter.submitList(items)
                                    }
                                }
                            }
                            GROUP_HEADER -> {
                                if (config.uid == it.owner) {
                                    it.guid?.let { guid ->
                                        it.members?.let { members ->
                                            (viewModel as? FriendListFragmentViewModel)?.setDeleteGroup(
                                                guid, members
                                            )
                                        }
                                    }
                                } else {
                                    it.guid?.let { guid ->
                                        (viewModel as? FriendListFragmentViewModel)?.setRemoveGroupMember(
                                            config.uid, guid
                                        )
                                    }
                                }
                                items.removeAt(viewHolder.bindingAdapterPosition)
                                genericListAdapter.submitList(items)
                            }
                            else -> Unit
                        }
                    }
                }
            }
            ItemTouchHelper(simpleCallBack).attachToRecyclerView(binding.mainRecycler)

            (viewModel as? FriendListFragmentViewModel)?.getRemoveFriend()?.observe(viewLifecycleOwner){
                when {
                    (it.status == Status.SUCCESS) ->
                        Toast.makeText(activity, "Friend Removed", Toast.LENGTH_LONG).show()
                }
            }

            (viewModel as? FriendListFragmentViewModel)?.getDeleteGroup()?.observe(viewLifecycleOwner){
                when {
                    (it.status == Status.SUCCESS) ->
                        Toast.makeText(activity, "Group Deleted", Toast.LENGTH_LONG).show()
                }
            }

            (viewModel as? FriendListFragmentViewModel)?.getRemoveGroupMember()?.observe(viewLifecycleOwner){
                when {
                    (it.status == Status.SUCCESS) ->
                        Toast.makeText(activity, "Group Left", Toast.LENGTH_LONG).show()
                }
            }

            (viewModel as? FriendListFragmentViewModel)?.setGroups()

            (viewModel as? FriendListFragmentViewModel)?.getGroups()?.observe(viewLifecycleOwner) { groups ->
                val list = mutableListOf<Item>(HeaderItem("0",
                    requireContext().getString(R.string.friend_list),
                    true,
                    true,
                    FRIEND_HEADER))
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
                                FRIEND_HEADER
                            )
                        } else {
                            if (!isGroupHeaderAdded) {
                                isGroupHeaderAdded = true
                                list.add(HeaderItem("1",
                                    requireContext().getString(R.string.groups_list),
                                    false,
                                    true,
                                    GROUP_HEADER
                                ))
                                FriendNameItem(
                                    null,
                                    pair.second.groupName,
                                    pair.second.uid,
                                    pair.second.owner,
                                    pair.second.members,
                                    pair.second.groupTypes,
                                    GROUP_HEADER
                                )
                            } else {
                                FriendNameItem(
                                    null,
                                    pair.second.groupName,
                                    pair.second.uid,
                                    pair.second.owner,
                                    pair.second.members,
                                    pair.second.groupTypes,
                                    GROUP_HEADER
                                )
                            }
                        }
                        list.add(item)
                    }
                }

                if (!isGroupHeaderAdded) {
                    list.add(HeaderItem("1",
                        requireContext().getString(R.string.groups_list),
                        false,
                        true,
                        GROUP_HEADER
                    ))
                    isGroupHeaderAdded = true
                }
                this.items.clear()
                this.items.addAll(list)
                genericListAdapter.submitList(this.items)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}
