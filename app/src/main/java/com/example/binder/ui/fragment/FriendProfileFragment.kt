package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.OnActionListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.binder.ui.viewholder.ViewHolderFactory
import org.koin.android.ext.android.inject
import Status
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutFriendProfileFragmentBinding
import com.example.binder.ui.Item
import com.example.binder.ui.viewholder.InterestItem
import data.FriendProfileConfig
import data.HubConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import viewmodel.FriendProfileFragmentViewModel
import viewmodel.MainActivityViewModel


class FriendProfileFragment(override val config: FriendProfileConfig) : BaseFragment() {

    override val viewModel: ViewModel by viewModel<FriendProfileFragmentViewModel>()

    private var binding: LayoutFriendProfileFragmentBinding? = null

    private val viewHolderFactory: ViewHolderFactory by inject()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val listener = object: OnActionListener {}

    private val genericListAdapter: GenericListAdapter = GenericListAdapter(viewHolderFactory, listener)

    override val items: MutableList<Item> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutFriendProfileFragmentBinding.inflate(inflater, container, false)
        setupUi()
        return binding!!.root
    }

    @SuppressWarnings("LongMethod")
    private fun setupUi(){
        binding?.let { binding ->
            println(config.fruid)
            binding.title.text = SpannableStringBuilder().apply {
                this.append(requireContext().getString(R.string.friend) + " ")
                val nameText = SpannableString(requireContext().getString(R.string.profile))
                nameText.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.app_yellow)),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(nameText)
            }

            (viewModel as FriendProfileFragmentViewModel).setSpecificUserInformation(config.fruid)
            (viewModel as FriendProfileFragmentViewModel).getSpecificUserInformation().observe(viewLifecycleOwner)
                { friend ->
                    if (friend.status == Status.SUCCESS && friend.data != null) {
                        binding.whatNameText.text = friend.data.name
                        binding.whatProgramText.text = friend.data.program
                        binding.whatSchoolText.text = friend.data.school
                        items.clear()
                        friend.data.interests?.forEach {
                            items.add(InterestItem(null, it, false))
                        }
                        genericListAdapter.submitList(items)
                }
            }

            binding.interestRecycler.layoutManager = LinearLayoutManager(context)
            binding.interestRecycler.adapter = genericListAdapter

            binding.unfriendButton.setOnClickListener{
                (viewModel as FriendProfileFragmentViewModel).setRemoveFriendId(config.fruid, config.guid)
                (viewModel as FriendProfileFragmentViewModel).getRemoveFriend().observe(viewLifecycleOwner){
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
}
