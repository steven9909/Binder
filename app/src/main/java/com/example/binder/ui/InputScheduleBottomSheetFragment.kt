package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.R
import com.example.binder.databinding.LayoutInputScheduleBottomSheetFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import data.InputScheduleBottomSheetConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.InputScheduleBottomSheetViewModel

class InputScheduleBottomSheetFragment(val config: InputScheduleBottomSheetConfig) : BottomSheetDialogFragment() {

    private val viewModel: ViewModel by viewModel<InputScheduleBottomSheetViewModel>()

    private var binding: LayoutInputScheduleBottomSheetFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutInputScheduleBottomSheetFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}
