package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutEditUserFragmentBinding
import com.example.binder.databinding.LayoutHubFragmentBinding
import data.Config
import data.EditUserConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.EditUserFragmentViewModel
import viewmodel.MainActivityViewModel

class EditUserFragment(override val config: EditUserConfig) : BaseFragment() {

    override val viewModel: ViewModel by viewModel<EditUserFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private var binding: LayoutEditUserFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutEditUserFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

}