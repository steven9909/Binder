package com.example.binder.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.R
import com.example.binder.databinding.LayoutAddFriendFragmentBinding
import com.example.binder.databinding.LayoutCalendarFragmentBinding
import data.AddFriendConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.AddFriendFragmentViewModel
import viewmodel.CalendarFragmentViewModel
import viewmodel.MainActivityViewModel

class AddFriendFragment(override val config: AddFriendConfig) : BaseFragment() {
    private var binding: LayoutAddFriendFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<AddFriendFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

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
                this.append(context?.getString(R.string.enter_name_recommended))
                val nameText = SpannableString(context?.getString(R.string.recommended_friends))
                nameText.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.app_yellow)),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(nameText)
            }
        }
    }
}