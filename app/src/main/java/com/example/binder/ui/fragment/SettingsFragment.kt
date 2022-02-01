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
import com.example.binder.R
import com.example.binder.ui.LocaleHelper
import com.example.binder.ui.MainActivity
import java.util.*

class SettingsFragment(override val config: SettingsConfig) : BaseFragment() {

    private var binding: LayoutSettingsBinding? = null

    override val viewModel: ViewModel by viewModel<FriendFinderFragmentViewModel>()

    var locale: String = Locale.getDefault().language

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
            val supportedLanguages =
                (activity as MainActivity).resources.getStringArray(R.array.languages)
            val spinner = binding.settingsLanguageSpinner
            val adapter = ArrayAdapter(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                supportedLanguages
            )
            spinner.adapter = adapter
            val adapterPos = adapter.getPosition(Locale.getDefault().displayLanguage)
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

            binding.settingPermissionButton.setOnClickListener{
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts(
                    "package",
                    this.requireContext().packageName,
                    null
                )
                intent.data = uri
                startActivity(intent)
            }

            binding.settingsButton.setOnClickListener{
                (activity as MainActivity).setAppLocale(locale)
                val intent = Intent(this.context, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
