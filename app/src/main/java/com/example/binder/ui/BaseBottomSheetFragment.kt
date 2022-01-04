package com.example.binder.ui

import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import data.Config

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {
    abstract val config: Config
    abstract val viewModel: ViewModel
}