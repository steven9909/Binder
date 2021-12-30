package com.example.binder.ui

import androidx.lifecycle.ViewModel
import data.Config
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.EditUserFragmentViewModel
import viewmodel.MainActivityViewModel

class EditUserFragment(override val config: Config) : BaseFragment() {

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    override val viewModel: ViewModel by viewModel<EditUserFragmentViewModel>()

}