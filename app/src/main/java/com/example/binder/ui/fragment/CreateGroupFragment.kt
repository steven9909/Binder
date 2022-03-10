package com.example.binder.ui.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutCreateGroupFragmentBinding
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.GroupTypeItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.CreateGroupConfig
import data.HubConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.CreateGroupFragmentViewModel
import viewmodel.MainActivityViewModel

class CreateGroupFragment(override val config: CreateGroupConfig) : BaseFragment() {

    companion object{
        private const val VERTICAL_SPACING = 25
    }

    private var binding: LayoutCreateGroupFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<CreateGroupFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var listAdapter: GenericListAdapter

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(item: Item) {
            (item as? FriendDetailItem)?.let {
                it.uid?.let { uid -> (viewModel as CreateGroupFragmentViewModel).addMember(uid) }
            }
        }
        override fun onViewUnSelected(item: Item) {
            (item as? FriendDetailItem)?.let {
                it.uid?.let { uid -> (viewModel as CreateGroupFragmentViewModel).removeMember(uid) }
            }
        }
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
        binding = LayoutCreateGroupFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    @SuppressWarnings("LongMethod", "ComplexMethod", "MagicNumber")
    private fun setUpUi(){
        binding?.let { binding ->
            binding.title.text = SpannableStringBuilder().apply {
                this.append(requireContext().getString(R.string.create) + " ")
                val nameText = SpannableString(requireContext().getString(R.string.group))
                nameText.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.app_yellow)),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(nameText)
            }

            listAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.friendListRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendListRecycler.adapter = listAdapter
            binding.friendListRecycler.addItemDecoration(VerticalSpaceItemDecoration(
                VERTICAL_SPACING
            ))

            (viewModel as CreateGroupFragmentViewModel).getFriends().observe(viewLifecycleOwner) {
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
                                friend.interests?.joinToString(", ") { interest -> interest } ?: ""
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
                (viewModel as CreateGroupFragmentViewModel).getFriendsStartingWith(name)
            }
            (viewModel as CreateGroupFragmentViewModel).getCreateGroup().observe(viewLifecycleOwner) {
                when {
                    (it.status == Status.SUCCESS) ->
                        mainActivityViewModel.postNavigation(HubConfig(config.name, config.uid))
                    (it.status == Status.ERROR) ->
                        Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                }
            }

            binding.sendRequestButton.setOnClickListener {
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
                (viewModel as CreateGroupFragmentViewModel).addMember(config.uid)
                (viewModel as CreateGroupFragmentViewModel).createGroup(
                    name,
                    config.uid,
                    types.map { it.groupType }
                )
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

                items.add(GroupTypeItem(null, binding.groupType.text.toString(), true))
                genericListAdapter.submitList(items)
                binding.groupType.text.clear()
            }
            binding.groupTypeRecycler.layoutManager = LinearLayoutManager(context)
            binding.groupTypeRecycler.adapter = genericListAdapter
        }
    }
}
