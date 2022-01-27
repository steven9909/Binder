package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutEditUserFragmentBinding
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.OnActionListener
import data.EditUserConfig
import data.User
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.EditUserFragmentViewModel
 import com.example.binder.ui.viewholder.ViewHolderFactory
import org.koin.android.ext.android.inject
import Status
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.ui.Item
import com.example.binder.ui.viewholder.InterestItem
import data.HubConfig
import observeOnce
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import viewmodel.MainActivityViewModel


class EditUserFragment(override val config: EditUserConfig) : BaseFragment() {

    override val viewModel: ViewModel by viewModel<EditUserFragmentViewModel>()

    private var binding: LayoutEditUserFragmentBinding? = null

    private lateinit var userInfo: User

    private val viewHolderFactory: ViewHolderFactory by inject()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val listener = object: OnActionListener {
        override fun onDeleteRequested(index: Int) {
            items.removeAt(index)
            genericListAdapter.submitList(items)
        }
    }

    private val genericListAdapter: GenericListAdapter = GenericListAdapter(viewHolderFactory, listener)

    override val items: MutableList<Item> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutEditUserFragmentBinding.inflate(inflater, container, false)
        setupUi()
        return binding!!.root
    }


    private fun setupUi(){
        binding?.let { binding ->
            (viewModel as EditUserFragmentViewModel).getUserInformation().observe(viewLifecycleOwner){ user->
                if (user.status == Status.SUCCESS && user.data != null) {
                    userInfo = user.data
                    binding.whatNameEdit.setText(userInfo.name)
                    binding.whatProgramEdit.setText(userInfo.program)
                    binding.whatSchoolEdit.setText(userInfo.school)
                    items.clear()
                    userInfo.interests?.forEach {
                        items.add(InterestItem(null, it))
                    }
                    genericListAdapter.submitList(items)
                }
            }

            binding.sendInterestButton.setOnClickListener {
                if (binding.whatInterestEdit.text.isBlank()) {
                    return@setOnClickListener
                }
                items.add(InterestItem(null, binding.whatInterestEdit.text.toString()))
                genericListAdapter.submitList(items)
                binding.whatInterestEdit.text.clear()
            }
            binding.interestRecycler.layoutManager = LinearLayoutManager(context)
            binding.interestRecycler.adapter = genericListAdapter

            binding.confirmChangeButton.setOnClickListener{
                if (binding.whatSchoolEdit.text.isBlank() ||
                    binding.whatProgramEdit.text.isBlank() ||
                    items.filterIsInstance(InterestItem::class.java).isEmpty()
                ) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.fields_cannot_be_empty),
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    (viewModel as EditUserFragmentViewModel).setUpdateUserInformation(
                        User(
                            binding.whatSchoolEdit.text.toString(),
                            binding.whatProgramEdit.text.toString(),
                            items.filterIsInstance(InterestItem::class.java).map { it.interest },
                            binding.whatNameEdit.text.toString(),
                            userGroups = userInfo.userGroups,
                            uid = userInfo.uid
                        )
                    )
                    config.name = binding.whatNameEdit.text.toString()
                }
            }

            (viewModel as EditUserFragmentViewModel).getUpdateUserInformation().observeOnce(viewLifecycleOwner){
                when {
                    (it.status == Status.SUCCESS) ->
                        mainActivityViewModel.postNavigation(HubConfig(config.name, config.uid))

                    (it.status == Status.ERROR) ->
                        Toast.makeText(
                            requireContext(),
                            requireContext().getString(R.string.update_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }
    }
}
