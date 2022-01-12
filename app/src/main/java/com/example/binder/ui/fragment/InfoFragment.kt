package com.example.binder.ui.fragment

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
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binder.R
import com.example.binder.databinding.LayoutInfoFragmentBinding
import com.example.binder.ui.ListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.InterestItem
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.HubConfig
import data.InfoConfig
import data.User
import observeOnce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.InfoFragmentViewModel
import viewmodel.MainActivityViewModel

class InfoFragment(override val config: InfoConfig) : BaseFragment() {

    private var binding: LayoutInfoFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<InfoFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private val viewHolderFactory: ViewHolderFactory by inject()

    private val listener = object: OnActionListener {
        override fun onDeleteRequested(index: Int) {
            listAdapter.deleteItemAt(index)
        }
    }

    private val listAdapter: ListAdapter = ListAdapter(viewHolderFactory, listener)

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
                if (binding.whatSchoolEdit.text.isBlank() ||
                    binding.whatProgramEdit.text.isBlank() ||
                    binding.whatInterestEdit.text.isBlank()
                ) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.fields_cannot_be_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                mainActivityViewModel.postLoadingScreenState(true)
                (viewModel as InfoFragmentViewModel).setUserInformation(User(
                    binding.whatSchoolEdit.text.toString(),
                    binding.whatProgramEdit.text.toString(),
                    binding.whatInterestEdit.text.toString(),
                    name = config.name,
                    userGroups = emptyList(),
                    uid = config.uid
                ))
                (viewModel as InfoFragmentViewModel).getUserLiveData().observeOnce(viewLifecycleOwner) {
                    when (it.status) {
                        Status.LOADING -> mainActivityViewModel.postLoadingScreenState(true)
                        Status.SUCCESS -> {
                            mainActivityViewModel.postLoadingScreenState(false)
                            mainActivityViewModel.postNavigation(HubConfig(config.name, config.uid))
                        }
                        Status.ERROR -> mainActivityViewModel.postLoadingScreenState(false)
                    }
                }
            }
            binding.sendInterestButton.setOnClickListener {
                listAdapter.insertItemEnd(InterestItem(binding.whatInterestEdit.text.toString()))
                binding.whatInterestEdit.text.clear()
            }
            binding.interestRecycler.layoutManager = LinearLayoutManager(context)
            binding.interestRecycler.adapter = listAdapter
        }
    }
}
