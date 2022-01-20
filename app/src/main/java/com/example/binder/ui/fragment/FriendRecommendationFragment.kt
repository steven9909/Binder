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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutFriendRecommendationFragmentBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.FriendRecommendationConfig
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
        override fun onViewSelected(index: Int, clickInfo: ClickInfo?) {
            (viewModel as? FriendRecommendationFragmentViewModel)?.addMarkedIndex(index)
        }

        override fun onViewUnSelected(index: Int, clickInfo: ClickInfo?) {
            (viewModel as? FriendRecommendationFragmentViewModel)?.removeMarkedIndex(index)
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
                this.append(requireContext().getString(R.string.friend_recommendation_title) + " ")
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

            listAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.friendRecommendationRecycler.layoutManager = LinearLayoutManager(context)
            binding.friendRecommendationRecycler.adapter = listAdapter
            binding.friendRecommendationRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )
        }
    }
}
