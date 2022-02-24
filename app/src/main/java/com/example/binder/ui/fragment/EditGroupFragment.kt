package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutEditGroupFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.GroupTypeItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.EditGroupConfig
import observeOnce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.CreateGroupFragmentViewModel
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
        override fun onViewSelected(index: Int, clickInfo: ClickInfo?) {
            clickInfo?.getSource()
                ?.let { (viewModel as CreateGroupFragmentViewModel).addMember(it) }
        }
        override fun onViewUnSelected(index: Int, clickInfo: ClickInfo?) {
            clickInfo?.getSource()
                ?.let { (viewModel as CreateGroupFragmentViewModel).removeMember(it) }
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
        binding = LayoutEditGroupFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    @SuppressWarnings("LongMethod", "ComplexMethod", "MagicNumber")
    private fun setUpUi(){
        binding?.let { binding ->

            listAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.friendListRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendListRecycler.adapter = listAdapter
            binding.friendListRecycler.addItemDecoration(VerticalSpaceItemDecoration(
                VERTICAL_SPACING
            ))

            binding.groupEdit.setText(config.chatName)

            config.groupTypes?.let { items.addAll(0, it) }
            genericListAdapter.submitList(items)
            binding.groupTypeRecycler.layoutManager = LinearLayoutManager(context)
            binding.groupTypeRecycler.adapter = genericListAdapter

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
}
