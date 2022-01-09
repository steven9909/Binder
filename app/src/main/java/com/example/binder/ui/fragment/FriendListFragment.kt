package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.databinding.LayoutFriendListFragmentBinding
import com.example.binder.databinding.LayoutVideoMenuFragmentBinding
import com.example.binder.ui.ListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.Config
import data.FriendListConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.AddFriendFragmentViewModel
import viewmodel.FriendListFragmentViewModel
import viewmodel.HubFragmentViewModel
import viewmodel.MainActivityViewModel

class FriendListFragment(override val config: FriendListConfig) : BaseFragment() {

    companion object{
        private const val VERTICAL_SPACING = 10
    }

    override val viewModel: ViewModel by viewModel<FriendListFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private var binding: LayoutFriendListFragmentBinding? = null

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var listAdapter: ListAdapter

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(index: Int) {

        }

        override fun onViewUnSelected(index: Int) {

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

    private fun setUpUi() {
        binding?.let { binding ->
            listAdapter = ListAdapter(viewHolderFactory, actionListener)

            binding.mainRecycler.layoutManager = LinearLayoutManager(context)
            binding.mainRecycler.adapter = listAdapter
            binding.mainRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )

        }
    }
}