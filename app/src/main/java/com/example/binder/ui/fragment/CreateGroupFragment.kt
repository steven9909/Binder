package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.databinding.LayoutCreateGroupBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.CreateGroupConfig
import data.FriendListConfig
import observeOnce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.CreateGroupFragmentViewModel
import viewmodel.MainActivityViewModel

class CreateGroupFragment(override val config: CreateGroupConfig) : BaseFragment() {

    companion object{
        private const val VERTICAL_SPACING = 25
    }

    private var binding: LayoutCreateGroupBinding? = null

    override val viewModel: ViewModel by viewModel<CreateGroupFragmentViewModel>()

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
    }

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
                                friend.uid,
                                friend.name ?: "",
                                friend.school ?: "",
                                friend.program ?: "",
                                friend.interests ?: ""
                            )
                        })
                    }

                    (it.status == Status.ERROR) -> {
                        listAdapter.clear()
                        binding.emptyView.visibility = View.VISIBLE
                    }
                }
            }
            binding.searchButton.setOnClickListener {
                val name = binding.friendEdit.text.toString()
                (viewModel as CreateGroupFragmentViewModel).getFriendsStartingWith(name)
            }

            (viewModel as CreateGroupFragmentViewModel).getCreateGroup().observeOnce(viewLifecycleOwner) {
                when {
                    (it.status == Status.SUCCESS) ->
                        mainActivityViewModel.postNavigation(
                            FriendListConfig(config.name, config.uid)
                        )

                    (it.status == Status.ERROR) ->
                        Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                }
            }
            binding.sendRequestButton.setOnClickListener {
                val name = binding.groupEdit.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(activity, "Error: require group name", Toast.LENGTH_LONG).show()
                } else {
                    val uid = config.uid
                    (viewModel as CreateGroupFragmentViewModel).addMember(uid)
                    (viewModel as CreateGroupFragmentViewModel).createGroup(name)
                }
            }
        }
    }
}
