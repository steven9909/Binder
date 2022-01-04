package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.R
import com.example.binder.databinding.LayoutScheduleDisplayBottomSheetFragmentBinding
import data.ScheduleDisplayBottomSheetConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.ScheduleDisplayBottomSheetViewModel

class ScheduleDisplayBottomSheetFragment(override val config: ScheduleDisplayBottomSheetConfig): BaseBottomSheetFragment() {

    override val viewModel: ViewModel by viewModel<ScheduleDisplayBottomSheetViewModel>()

    private var binding: LayoutScheduleDisplayBottomSheetFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutScheduleDisplayBottomSheetFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

}