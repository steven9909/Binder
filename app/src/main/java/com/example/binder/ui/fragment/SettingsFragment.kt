package com.example.binder.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutSettingsBinding
import data.SettingsConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.FriendFinderFragmentViewModel
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.example.binder.ui.MainActivity
import java.util.*

class SettingsFragment(override val config: SettingsConfig) : BaseFragment() {

    private var binding: LayoutSettingsBinding? = null

    override val viewModel: ViewModel by viewModel<FriendFinderFragmentViewModel>()

    private val supportLanguage = mutableListOf("English", "Korean")

    var locale = Locale.getDefault().getLanguage()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutSettingsBinding.inflate(inflater, container, false)
        setupUi()
        return binding!!.root
    }

    private fun setupUi() {
        binding?.let { binding ->
            val spinner = binding.settingsLanguageSpinner
            val adapter = ArrayAdapter(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                supportLanguage
            )
            spinner.adapter = adapter
            val adapterPos = adapter.getPosition(Locale.getDefault().getDisplayName())
            spinner.setSelection(adapterPos)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    pos: Int,
                    id: Long
                ) {
                    when (pos) {
                        0 -> locale = "en"
                        1 -> locale = "ko"
                    }

                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            binding.settingPermissionButton.setOnClickListener(){
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts(
                    "package",
                    this.requireContext().packageName,
                    null
                )
                intent.data = uri
                startActivity(intent)
            }

            binding.settingsButton.setOnClickListener(){
                (activity as MainActivity).setLanguage(locale)
            }
        }
    }
}
