package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.R
import com.example.binder.databinding.LayoutInputQuestionBottomSheetFragmentBinding
import com.example.binder.databinding.LayoutInputScheduleBottomSheetFragmentBinding
import data.InputQuestionBottomSheetConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.InputQuestionBottomSheetViewModel

class InputQuestionBottomSheetFragment(
    override val config: InputQuestionBottomSheetConfig) : BaseBottomSheetFragment() {
    override val viewModel: ViewModel by viewModel<InputQuestionBottomSheetViewModel>()

    private var binding: LayoutInputQuestionBottomSheetFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutInputQuestionBottomSheetFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    private fun setUpUi() {
        binding?.let { binding ->




        }
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}