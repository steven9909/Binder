package com.example.binder.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import data.SettingsConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.example.binder.R
import com.example.binder.databinding.LayoutSettingsFragmentBinding
import com.example.binder.ui.MainActivity
import viewmodel.SettingsFragmentViewModel
import java.util.*

class SettingsFragment(override val config: SettingsConfig) : BaseFragment() {

    private var binding: LayoutSettingsFragmentBinding? = null

    override val viewModel: ViewModel by viewModel<SettingsFragmentViewModel>()

    var locale: String = Locale.getDefault().language

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutSettingsFragmentBinding.inflate(inflater, container, false)
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
                override fun onNothingSelected(parent: AdapterView<*>) {
                    Unit
                }
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

            binding.settingNotificationButton.setOnClickListener{
                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, this.requireContext().packageName)
                startActivity(settingsIntent)
            }

            binding.settingsButton.setOnClickListener{
                (activity as MainActivity).setAppLocale(locale)
                val intent = Intent(this.requireContext(), MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
