package com.example.binder.ui.fragment

import androidx.lifecycle.ViewModel
import data.Config

class EmptyFragment(override val config: Config) : BaseFragment() {
    override val viewModel: ViewModel
        get() = TODO("Not yet implemented")
}
