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
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutFriendRecommendationFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.FriendDetailItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.FriendRecommendationConfig
import data.HubConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.FriendRecommendationFragmentViewModel
import viewmodel.MainActivityViewModel

class FriendRecommendationFragment (override val config: FriendRecommendationConfig) : BaseFragment() {

    companion object {
        private const val VERTICAL_SPACING = 25
    }

    private var binding: LayoutFriendRecommendationFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<FriendRecommendationFragmentViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private lateinit var listAdapter: GenericListAdapter

    private val actionListener = object : OnActionListener {
        override fun onViewSelected(item: Item) {
            (item as? FriendDetailItem)?.let {
                (viewModel as? FriendRecommendationFragmentViewModel)?.addMarkedIndex(item.uid)
            }
        }

        override fun onViewUnSelected(item: Item) {
            (item as? FriendDetailItem)?.let {
                (viewModel as? FriendRecommendationFragmentViewModel)?.removeMarkedIndex(item.uid)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutFriendRecommendationFragmentBinding.inflate(inflater, container, false)
        setUpUi()
        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->
            val spannableString = SpannableStringBuilder().apply {
                this.append(requireContext().getString(R.string.friend_recommendation_title) + "\n")
                val nameText = SpannableString(requireContext().getString(R.string.get_friends_1))
                nameText.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.app_yellow)),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(nameText)
            }
            binding.titleText.text = spannableString

            binding.sendRequestButton.setOnClickListener {
                (viewModel as? FriendRecommendationFragmentViewModel)?.setFriendRequestParam()
            }

            (viewModel as? FriendRecommendationFragmentViewModel)?.getFriendRequest()?.observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    listAdapter.submitList(emptyList())
                    (viewModel as? FriendRecommendationFragmentViewModel)?.setRecommendationParam(config.uid)
                }
            }

            listAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.friendRecommendationRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendRecommendationRecycler.adapter = listAdapter
            binding.friendRecommendationRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )

            (viewModel as? FriendRecommendationFragmentViewModel)?.setRecommendationParam(config.uid)
            (viewModel as? FriendRecommendationFragmentViewModel)
                ?.getRecommendation()
                ?.observe(viewLifecycleOwner) { result ->
                    if (result.status == Status.SUCCESS && result.data != null) {
                        listAdapter.submitList(result.data.sortedByDescending { it.score }.map { data ->
                            FriendDetailItem(
                                null,
                                data.userId,
                                data.name ?: "",
                                data.school ?: "",
                                data.program ?: "",
                                data.interests?.joinToString(", ") { interest -> interest } ?: ""
                            )
                        })
                    } else {
                        Toast.makeText(requireContext(), "Recommendation Fetch Failed", LENGTH_SHORT).show()
                    }
            }
        }
    }
}
