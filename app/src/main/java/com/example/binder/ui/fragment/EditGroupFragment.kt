package com.example.binder.ui.fragment

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
import com.example.binder.databinding.LayoutEditGroupFragmentBinding
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.FriendDetailViewHolder
import com.example.binder.ui.viewholder.FriendNameViewHolder
import com.example.binder.ui.viewholder.GroupTypeItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.EditGroupConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import setVisibility
import viewmodel.EditGroupFragmentViewModel
import viewmodel.MainActivityViewModel

class EditGroupFragment(override val config: EditGroupConfig) : BaseFragment() {

    companion object{
        private const val VERTICAL_SPACING = 25
    }

    private var binding: LayoutEditGroupFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<EditGroupFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var listAdapter: GenericListAdapter

    private val actionListener = object: OnActionListener {
        override fun onDeleteRequested(index: Int) {
            items.removeAt(index)
            genericListAdapter.submitList(items)
        }
    }

    private val genericListAdapter: GenericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)

    override val items: MutableList<Item> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutEditGroupFragmentBinding.inflate(inflater, container, false)

        binding?.let { binding ->
            setUpOwnerUi()
//            if (config.uid == config.owner) {
//                binding.ownerContent.visibility = View.VISIBLE
//                binding.memberContent.visibility = View.GONE
//                setUpOwnerUi()
//            } else {
//                binding.ownerContent.visibility = View.GONE
//                binding.memberContent.visibility = View.VISIBLE
//                setUpMemberUi()
//            }
        }

        return binding!!.root
    }

    @SuppressWarnings("LongMethod", "ComplexMethod", "MagicNumber")
    private fun setUpOwnerUi(){
        binding?.let { binding ->

            listAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.memberListRecycler.layoutManager = LinearLayoutManager(context)
            binding.memberListRecycler.adapter = listAdapter
            binding.memberListRecycler.addItemDecoration(VerticalSpaceItemDecoration(
                VERTICAL_SPACING
            ))

            binding.groupEdit.setText(config.chatName)

            config.groupTypes?.map {
                GroupTypeItem(null, it)
            }?.let { items.addAll(0, it) }
            genericListAdapter.submitList(items)

            config.members?.let{ members ->
               for(member in members) {
                   (viewModel as EditGroupFragmentViewModel).setSpecificUserInformation(member)
               }

            }

            (viewModel as EditGroupFragmentViewModel).getSpecificUserInformation().observe(viewLifecycleOwner) {
                when {
                    (it.status == Status.SUCCESS && it.data != null) -> {
                        (viewModel as EditGroupFragmentViewModel).addMember(
                            FriendDetailItem(
                                null,
                                it.data.uid,
                                it.data.name ?: "",
                                it.data.school ?: "",
                                it.data.program ?: "",
                                it.data.interests?.joinToString(", ") { interest -> interest } ?: ""
                            )
                        )
                    }
                }
                listAdapter.submitList((viewModel as EditGroupFragmentViewModel).getMembers())
            }

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
                        is FriendDetailViewHolder -> super.getSwipeDirs(recyclerView, viewHolder)
                        else -> 0
                    }
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    (genericListAdapter.getItemAt(viewHolder.bindingAdapterPosition) as? FriendDetailItem)?.let {
                        (viewModel as EditGroupFragmentViewModel).removeMember(it)
                        listAdapter.submitList((viewModel as EditGroupFragmentViewModel).getMembers())
                    }
                }
            }
            ItemTouchHelper(simpleCallBack).attachToRecyclerView(binding.memberListRecycler)

            println((viewModel as EditGroupFragmentViewModel).getMembers().toString())

            listAdapter.submitList((viewModel as EditGroupFragmentViewModel).getMembers())

            binding.confirmChangeButton.setOnClickListener {
                val name = binding.groupEdit.text.toString()
                val types = items.filterIsInstance(GroupTypeItem::class.java)
                if (name.isEmpty() || types.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.fields_cannot_be_empty),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                mainActivityViewModel.postLoadingScreenState(true)
                // call setUpdateGroupInformation
            }

            binding.sendGroupTypeButton.setOnClickListener {
                if (binding.groupType.text.isBlank()) {
                    return@setOnClickListener
                }
                val type = binding.groupType.text.toString()
                if (type.length > 6) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.group_type_too_long),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                items.add(GroupTypeItem(null, binding.groupType.text.toString()))
                genericListAdapter.submitList(items)
                binding.groupType.text.clear()
            }
            binding.groupTypeRecycler.layoutManager = LinearLayoutManager(context)
            binding.groupTypeRecycler.adapter = genericListAdapter
        }
    }

    @SuppressWarnings("LongMethod", "ComplexMethod", "MagicNumber")
    private fun setUpMemberUi() {
        binding?.let { binding ->

        }
    }
}
