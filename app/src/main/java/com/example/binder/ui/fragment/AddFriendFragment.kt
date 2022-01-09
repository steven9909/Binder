package com.example.binder.ui.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.databinding.LayoutAddFriendFragmentBinding
import com.example.binder.ui.ListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.AddFriendConfig
import data.HubConfig
import observeOnce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.AddFriendFragmentViewModel
import viewmodel.MainActivityViewModel

class AddFriendFragment(override val config: AddFriendConfig) : BaseFragment() {

    companion object{
        private const val VERTICAL_SPACING = 25
    }

    private var binding: LayoutAddFriendFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<AddFriendFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var listAdapter: ListAdapter

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(index: Int) {
            (viewModel as AddFriendFragmentViewModel).addMarkedIndex(index)
        }

        override fun onViewUnSelected(index: Int) {
            (viewModel as AddFriendFragmentViewModel).removeMarkedIndex(index)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutAddFriendFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->
            binding.enterNameTitle.text = SpannableStringBuilder().apply {
                this.append(requireContext().getString(R.string.enter_name_recommended) + " ")
                val nameText = SpannableString(requireContext().getString(R.string.recommended_friends))
                nameText.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.app_yellow)),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(nameText)
            }
            binding.searchButton.setOnClickListener {
                val name = binding.nameEdit.text.toString()
                (viewModel as AddFriendFragmentViewModel).fetchUsersStartingWith(name)
            }
            binding.sendRequestButton.setOnClickListener {
                (viewModel as AddFriendFragmentViewModel).addFriends(config.name, config.uid)
                (viewModel as AddFriendFragmentViewModel).getAddFriends().observeOnce(this) {
                    when {
                        (it.status == Status.SUCCESS) ->
                            mainActivityViewModel.postNavigation(HubConfig(config.name, config.uid))
                    }
                }
            }

            listAdapter = ListAdapter(viewHolderFactory, actionListener)

            binding.friendListRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendListRecycler.adapter = listAdapter
            binding.friendListRecycler.addItemDecoration(VerticalSpaceItemDecoration(
                VERTICAL_SPACING
            ))

            (viewModel as AddFriendFragmentViewModel).getUsers().observe(viewLifecycleOwner) {
                when {
                    (it.status == Status.SUCCESS && it.data != null) -> {
                        listAdapter.updateItems(it.data.map { user ->
                            FriendDetailItem(user.userId, user.name ?: "", user.school, user.program, user.interests)
                        })
                    }
                }
            }
        }
    }
}
