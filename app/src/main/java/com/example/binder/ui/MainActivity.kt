package com.example.binder.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.binder.R
import com.example.binder.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import data.HubConfig
import data.LoginConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModel<MainActivityViewModel>()

    private lateinit var binding: ActivityMainBinding

    private val googleAccountProvider: GoogleAccountProvider by inject()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        if (googleAccountProvider.tryGetAccount() == null || Firebase.auth.currentUser == null || Firebase.auth.uid == null) {
            return false
        }
        return true
    }

    fun getNameFromGoogleSignIn(): String =
        googleAccountProvider.tryGetAccount()?.let {
            (it.givenName ?: "") + " " + (it.familyName ?: "")
        } ?: ""

}
