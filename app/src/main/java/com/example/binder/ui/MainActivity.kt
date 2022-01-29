package com.example.binder.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.binder.R
import com.example.binder.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import data.HubConfig
import data.LoginConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.MainActivityViewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModel<MainActivityViewModel>()

    private lateinit var binding: ActivityMainBinding

    private val googleAccountProvider: GoogleAccountProvider by inject()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mainViewModel.mappedFragmentLiveData().observe(this) { fragmentCarrier ->
            when {
                fragmentCarrier.isBottomSheet -> {
                    (fragmentCarrier.fragment as? BottomSheetDialogFragment)
                        ?.show(supportFragmentManager, fragmentCarrier.fragment.tag)
                }
                fragmentCarrier.shouldBeAddedToBackStack -> {
                    supportFragmentManager
                        .beginTransaction()
                        .add(R.id.main_fragment, fragmentCarrier.fragment)
                        .addToBackStack(fragmentCarrier.fragment.tag)
                        .commit()
                }
                else -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragmentCarrier.fragment)
                        .commit()
                }
            }
        }
        mainViewModel.getLoadingLiveData().observe(this) { isLoading ->
            binding.loadingScreen.isVisible = !isLoading
        }

        if (!isUserLoggedIn()) {
            mainViewModel.postNavigation(LoginConfig())
        }

        Firebase.auth.uid?.let { uid ->
            mainViewModel.postNavigation(HubConfig(getNameFromGoogleSignIn(), uid))
        } ?: run {
            mainViewModel.postNavigation(LoginConfig())
        }

        mainViewModel.getCloudMessagingToken().observe(this) {
            if (it.status == Status.SUCCESS) {
                Unit
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        if (googleAccountProvider.tryGetAccount() == null ||
            Firebase.auth.currentUser == null
            || Firebase.auth.uid == null
        ) {
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLocale(localeToSet: String) {
        val localeListToSet = LocaleList(Locale(localeToSet))
        LocaleList.setDefault(localeListToSet)
        resources.configuration.setLocales(localeListToSet)
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        sharedPref.putString("locale_to_set", localeToSet)
        sharedPref.apply()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadLocale() {
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val localeToSet: String = sharedPref.getString("locale_to_set", "")!!
        setLocale(localeToSet)
    }

    fun getNameFromGoogleSignIn(): String =
        googleAccountProvider.tryGetAccount()?.let {
            (it.givenName ?: "") + " " + (it.familyName ?: "")
        } ?: ""

}
