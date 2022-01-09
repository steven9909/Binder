package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutFriendListFragmentBinding
import com.example.binder.databinding.LayoutVideoMenuFragmentBinding
import data.Config
import data.FriendListConfig
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.FriendListFragmentViewModel
import viewmodel.HubFragmentViewModel
import viewmodel.MainActivityViewModel

class FriendListFragment(override val config: FriendListConfig) : BaseFragment() {
    override val viewModel: ViewModel by viewModel<FriendListFragmentViewModel>()

    private val mainActivityViewModel by sharedViewModel<MainActivityViewModel>()

    private var binding: LayoutFriendListFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFriendListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }
}