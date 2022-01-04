package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutFriendFinderFragmentBinding
import data.FriendFinderConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.FriendFinderFragmentViewModel

class FriendFinderFragment(override val config: FriendFinderConfig) : BaseFragment() {

    private var binding: LayoutFriendFinderFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<FriendFinderFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFriendFinderFragmentBinding.inflate(inflater, container, false)

        return binding!!.root
    }
}
