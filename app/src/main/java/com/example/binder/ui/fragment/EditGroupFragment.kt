package com.example.binder.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutEditGroupFragmentBinding
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.GroupTypeItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.ChatConfig
import data.EditGroupConfig
import data.FriendListConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
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
        override fun onViewSelected(item: Item) {
            super.onViewSelected(item)
            (item as? FriendDetailItem)?.let {
                (viewModel as EditGroupFragmentViewModel).removeMember(it)
                it.uid?.let { user -> (viewModel as EditGroupFragmentViewModel).addRemoved(user) }
            }
        }
        override fun onViewUnSelected(item: Item) {
            super.onViewSelected(item)
            (item as? FriendDetailItem)?.let {
                (viewModel as EditGroupFragmentViewModel).addMember(it)
                it.uid?.let { user -> (viewModel as EditGroupFragmentViewModel).removeRemoved(user) }
            }
        }

        override fun onDeleteRequested(index: Int) {
            items.removeAt(index)
            listAdapter.submitList(items)
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
            if (config.uid == config.owner) {
                binding.ownerContent.visibility = View.VISIBLE
                binding.memberContent.visibility = View.GONE
                setUpOwnerUi()
            } else {
                binding.ownerContent.visibility = View.GONE
                binding.memberContent.visibility = View.VISIBLE
                setUpMemberUi()
            }
        }

        return binding!!.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                @SuppressWarnings("LongParameterList")
                override fun handleOnBackPressed() {
                    mainActivityViewModel.postNavigation(
                        ChatConfig(
                            config.name,
                            config.uid,
                            config.guid,
                            config.chatName,
                            config.owner,
                            config.members,
                            config.groupTypes)
                    )
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    @SuppressWarnings("LongMethod", "ComplexMethod", "MagicNumber")
    private fun setUpOwnerUi(){
        binding?.let { binding ->
            binding.groupEdit.setText(config.chatName)

            config.groupTypes?.map {
                GroupTypeItem(null, it, false)
            }?.let { items.addAll(0, it) }
            genericListAdapter.submitList(items)

            listAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.memberListRecycler.layoutManager = LinearLayoutManager(context)
            binding.memberListRecycler.adapter = listAdapter
            binding.memberListRecycler.addItemDecoration(VerticalSpaceItemDecoration(
                VERTICAL_SPACING
            ))

            val memberTitle = requireContext().getString(R.string.members)
                .plus(" ")
                .plus(getString(R.string.select_to_kick))
            binding.selectMemberTitle.text = memberTitle
            val itr = config.members.distinct().iterator()
            (viewModel as EditGroupFragmentViewModel).setSpecificUserInformation(itr.next())
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
                        listAdapter.submitList((viewModel as EditGroupFragmentViewModel).getMembers().distinct())
                        if (itr.hasNext()){
                            (viewModel as EditGroupFragmentViewModel).setSpecificUserInformation(itr.next())
                        }
                    }
                }
            }

            binding.confirmChangeButton.text = requireContext().getString(R.string.confirm_changes)
            binding.confirmChangeButton.setOnClickListener {
                val name = binding.groupEdit.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.fields_cannot_be_empty),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                } else if ((viewModel as EditGroupFragmentViewModel).getRemoved().contains(config.uid)) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.cannot_remove_owner),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                mainActivityViewModel.postLoadingScreenState(true)

                (viewModel as EditGroupFragmentViewModel).setUpdateGroupName(
                    config.guid,
                    binding.groupEdit.text.toString()
                )
                (viewModel as EditGroupFragmentViewModel).getUpdateGroupName().observe(viewLifecycleOwner){
                    when {
                        (it.status == Status.SUCCESS) -> {
                            config.chatName = binding.groupEdit.text.toString()
                            Toast.makeText(
                                activity,
                                requireContext().getString(R.string.update_success),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        (it.status == Status.ERROR) ->
                            Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                    }
                }

                if ((viewModel as EditGroupFragmentViewModel).getRemoved().isNotEmpty()){
                    (viewModel as EditGroupFragmentViewModel).setRemoveGroupMember(config.guid)
                }
                (viewModel as EditGroupFragmentViewModel).getRemoveGroupMember().observe(viewLifecycleOwner){
                    when {
                        (it.status == Status.SUCCESS) -> {
                            config.members = (viewModel as EditGroupFragmentViewModel).getMembers().map { member ->
                                member.uid!!
                            }
                            Toast.makeText(
                                activity,
                                requireContext().getString(R.string.update_success),
                                Toast.LENGTH_LONG
                            ).show()
                            listAdapter.submitList((viewModel as EditGroupFragmentViewModel).getMembers())
                        }

                        (it.status == Status.ERROR) ->
                            Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            binding.groupTypeRecycler.layoutManager = LinearLayoutManager(context)
            binding.groupTypeRecycler.adapter = genericListAdapter
        }
    }

    @SuppressWarnings("LongMethod", "ComplexMethod", "MagicNumber")
    private fun setUpMemberUi() {
        binding?.let { binding ->
            binding.groupNoneEdit.text = config.chatName

            config.groupTypes?.map {
                GroupTypeItem(null, it, false)
            }?.let { items.addAll(0, it) }
            genericListAdapter.submitList(items)

            binding.groupTypeRecyclerNoneEdit.layoutManager = LinearLayoutManager(context)
            binding.groupTypeRecyclerNoneEdit.adapter = genericListAdapter

            listAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.memberListRecyclerNoneEdit.layoutManager = LinearLayoutManager(context)
            binding.memberListRecyclerNoneEdit.adapter = listAdapter
            binding.memberListRecyclerNoneEdit.addItemDecoration(VerticalSpaceItemDecoration(
                VERTICAL_SPACING
            ))

            val itr = config.members.distinct().iterator()
            (viewModel as EditGroupFragmentViewModel).setSpecificUserInformation(itr.next())
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
                                it.data.interests?.joinToString(", ") { interest -> interest } ?: "",
                                 true
                            )
                        )
                        listAdapter.submitList((viewModel as EditGroupFragmentViewModel).getMembers().distinct())
                        if (itr.hasNext()){
                            (viewModel as EditGroupFragmentViewModel).setSpecificUserInformation(itr.next())
                        }
                    }
                }
            }

            binding.confirmChangeButton.text = requireContext().getString(R.string.leave_group)
            binding.confirmChangeButton.setOnClickListener {
                (viewModel as EditGroupFragmentViewModel).addRemoved(config.uid)
                (viewModel as EditGroupFragmentViewModel).setRemoveGroupMember(config.guid)
                (viewModel as EditGroupFragmentViewModel).getRemoveGroupMember().observe(viewLifecycleOwner){
                    when {
                        (it.status == Status.SUCCESS) ->
                            mainActivityViewModel.postNavigation(FriendListConfig(
                                config.name,
                                config.uid)
                            )
                        (it.status == Status.ERROR) ->
                            Toast.makeText(
                                activity,
                                requireContext().getString(R.string.update_failed),
                                Toast.LENGTH_LONG)
                                .show()
                    }
                }
            }
        }
    }
}
