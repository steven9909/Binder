package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.binder.databinding.LayoutFriendFinderFragmentBinding
import data.FriendFinderConfig
import data.InfoConfig

class FriendFinderFragment(override val config: FriendFinderConfig) : BaseFragment() {

    private var binding: LayoutFriendFinderFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFriendFinderFragmentBinding.inflate(inflater, container, false)

        return binding!!.root
    }


}