package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.binder.databinding.LayoutLoginFragmentBinding


class LoginFragment: BaseFragment() {

    private var binding: LayoutLoginFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutLoginFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}