package com.example.binder.ui

import androidx.lifecycle.ViewModel
import data.Config
import viewmodel.HubFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HubFragment(override val config: Config) : BaseFragment() {
    override val viewModel: ViewModel by viewModel<HubFragmentViewModel>()
}