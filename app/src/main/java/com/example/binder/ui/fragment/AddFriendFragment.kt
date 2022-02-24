package com.example.binder.ui.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.databinding.LayoutAddFriendFragmentBinding
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.AddFriendConfig
import data.FriendRecommendationConfig
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

    private lateinit var genericListAdapter: GenericListAdapter

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(item: Item) {
            super.onViewSelected(item)
            (item as? FriendDetailItem)?.let {
                (viewModel as AddFriendFragmentViewModel).addMarkedIndex(it.uid)
            }
        }

        override fun onViewUnSelected(item: Item) {
            super.onViewUnSelected(item)
            (item as? FriendDetailItem)?.let {
                (viewModel as AddFriendFragmentViewModel).removeMarkedIndex(it.uid)
            }
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

    @SuppressWarnings("LongMethod")
    private fun setUpUi() {
        binding?.let { binding ->
            val clickableSpan: ClickableSpan = object:ClickableSpan()  {
                override fun onClick(textView: View) {
                    mainActivityViewModel.postNavigation(FriendRecommendationConfig(config.name, config.uid))
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.setColor(requireContext().getColor(R.color.app_yellow))
                    ds.isUnderlineText = false
                }
            }
            val spannableString = SpannableStringBuilder().apply {
                this.append(requireContext().getString(R.string.enter_name_recommended) + " ")
                val nameText = SpannableString(requireContext().getString(R.string.recommended_friends))
                nameText.setSpan(
                    clickableSpan,
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(nameText)
            }
            binding.enterNameTitle.text = spannableString
            binding.enterNameTitle.movementMethod = LinkMovementMethod.getInstance()
            binding.searchButton.setOnClickListener {
                val name = binding.nameEdit.text.toString()
                (viewModel as AddFriendFragmentViewModel).fetchUsersStartingWith(name)
            }
            binding.sendRequestButton.setOnClickListener {
                (viewModel as AddFriendFragmentViewModel).sendUserFriendRequests()
                (viewModel as AddFriendFragmentViewModel).getAddFriends().observeOnce(this) {
                    when {
                        (it.status == Status.SUCCESS) ->
                            mainActivityViewModel.postNavigation(HubConfig(config.name, config.uid))
                    }
                }
            }

            genericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)

            binding.friendListRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendListRecycler.adapter = genericListAdapter
            binding.friendListRecycler.addItemDecoration(VerticalSpaceItemDecoration(
                VERTICAL_SPACING
            ))

            (viewModel as AddFriendFragmentViewModel).getUsers().observe(viewLifecycleOwner) { it ->
                when {
                    (it.status == Status.SUCCESS && it.data != null) -> {
                        genericListAdapter.submitList(it.data.map { user ->
                            FriendDetailItem(
                                null,
                                user.uid,
                                user.name ?: "",
                                user.school ?: "",
                                user.program ?: "",
                                user.interests?.joinToString(", ") { interest -> interest } ?: ""
                            )
                        })
                    }
                }
            }
        }
    }
}
