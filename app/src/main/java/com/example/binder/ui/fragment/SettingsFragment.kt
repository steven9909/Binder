package com.example.binder.ui.fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutFriendFinderFragmentBinding
import com.example.binder.databinding.LayoutSettingsBinding
import com.example.binder.ui.fragment.BaseFragment
import data.FriendFinderConfig
import data.InfoConfig
import data.SettingsConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.FriendFinderFragmentViewModel
import viewmodel.LoginFragmentViewModel

class SettingsFragment(override val config: SettingsConfig) : BaseFragment() {

    private var binding: LayoutSettingsBinding? = null

    override val viewModel: ViewModel by viewModel<FriendFinderFragmentViewModel>()

    private val langauge = arrayOf("English", "Korean")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutSettingsBinding.inflate(inflater, container, false)
        setupUi()
        return binding!!.root
    }

    private fun setupUi(){
        binding?.let { binding ->
            val spinner = binding.settingsLanguageSpinner
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,langauge)
        }
}
