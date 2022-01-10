package com.example.binder.ui.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import data.Config

abstract class BaseFragment: Fragment() {
    abstract val config: Config
    abstract val viewModel: ViewModel
}
