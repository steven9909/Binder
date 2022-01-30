package com.example.binder.ui.fragment


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutSettingsBinding
import com.example.binder.ui.MainActivity
import data.SettingsConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.FriendFinderFragmentViewModel


class SettingsFragment(override val config: SettingsConfig) : BaseFragment() {

    private var binding: LayoutSettingsBinding? = null

    override val viewModel: ViewModel by viewModel<FriendFinderFragmentViewModel>()

    private val langauge = mutableListOf("English", "Korean")

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutSettingsBinding.inflate(inflater, container, false)
        setupUi()
        return binding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupUi() {
        binding?.let { binding ->
            val spinner = binding.settingsLanguageSpinner
            val adapter = ArrayAdapter(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                langauge
            )
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    pos: Int,
                    id: Long
                ) {
                    when (pos) {
                        0 -> {
                            (activity as MainActivity).setLocale("en")
                        }
                        1 -> {
                            (activity as MainActivity).setLocale("ko")
                        }
                    }

                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            binding.settingsButton.setOnClickListener(){
                (activity as MainActivity).loadLocale()
            }
        }
    }
}
