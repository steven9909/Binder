package com.example.binder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.binder.databinding.LayoutInfoFragmentBinding

class InfoFragment : BaseFragment() {

    private var binding: LayoutInfoFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutInfoFragmentBinding.inflate(inflater, container, false)

        return binding!!.root
    }

}