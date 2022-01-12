package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.databinding.LayoutCreateGroupBinding
import com.example.binder.ui.ListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.CreateGroupConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.AddFriendFragmentViewModel
import viewmodel.CreateGroupFragmentViewModel

class CreateGroupFragment(override val config: CreateGroupConfig) : BaseFragment() {

    private var binding: LayoutCreateGroupBinding? = null

    override val viewModel: ViewModel by viewModel<CreateGroupFragmentViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(index: Int) {
            (viewModel as CreateGroupFragmentViewModel).addMarkedIndex(index)
        }
        override fun onViewUnSelected(index: Int) {
            (viewModel as CreateGroupFragmentViewModel).removeMarkedIndex(index)
        }
    }

    private lateinit var listAdapter: ListAdapter

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
            (viewModel as CreateGroupFragmentViewModel).getFriends()

            binding.searchButton.setOnClickListener {
                val name = binding.friendEdit.text.toString()
                (viewModel as CreateGroupFragmentViewModel).getFriendsStartingWith(name)
            }

            binding.sendRequestButton.setOnClickListener {
                val name = binding.groupEdit.text.toString()
                (viewModel as CreateGroupFragmentViewModel).createGroup(name)
            }

            binding.friendListRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendListRecycler.adapter = listAdapter
        }
    }
}