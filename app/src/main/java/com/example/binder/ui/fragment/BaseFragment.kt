package com.example.binder.ui.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.binder.ui.Item
import data.Config

abstract class BaseFragment: Fragment() {
    abstract val config: Config
    abstract val viewModel: ViewModel
    protected open val items: List<Item>? = null
}
