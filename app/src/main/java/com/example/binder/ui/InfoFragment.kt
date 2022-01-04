package com.example.binder.ui

import Status
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
import com.example.binder.databinding.LayoutInfoFragmentBinding
import data.HubConfig
import data.InfoConfig
import data.User
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.InfoFragmentViewModel
import viewmodel.MainActivityViewModel

class InfoFragment(override val config: InfoConfig) : BaseFragment() {

    private var binding: LayoutInfoFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<InfoFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutInfoFragmentBinding.inflate(inflater, container, false)

        setUpUi()

        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->
            binding.welcomeText.text = SpannableStringBuilder().apply {
                this.append(context?.getString(R.string.welcome) + "\n")
                val nameText = SpannableString(config.name)
                nameText.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                nameText.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.app_yellow)),
                    0,
                    nameText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.append(nameText)
            }
            binding.nextButton.setOnClickListener {
                mainActivityViewModel.postLoadingScreenState(true)
                val result = (viewModel as InfoFragmentViewModel).updateUserInformation(User(
                    config.uid,
                    binding.whatSchoolEdit.text.toString(),
                    binding.whatProgramEdit.text.toString(),
                    binding.whatInterestEdit.text.toString()
                ))
                result.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.LOADING -> mainActivityViewModel.postLoadingScreenState(true)
                        Status.SUCCESS -> {
                            mainActivityViewModel.postLoadingScreenState(false)
                            mainActivityViewModel.postNavigation(HubConfig(config.name))
                        }
                        Status.ERROR -> mainActivityViewModel.postLoadingScreenState(false)
                    }
                }
            }
        }
    }

}
