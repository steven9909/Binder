package com.example.binder.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import com.example.binder.R
import com.example.binder.databinding.ActivityMainBinding
import com.example.binder.ui.fragment.ChatFragment
import com.example.binder.ui.fragment.FriendRequestFragment
import com.example.binder.ui.fragment.HubFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import data.ChatConfig
import data.FriendRequestConfig
import data.HubConfig
import data.LoginConfig
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModel<MainActivityViewModel>()

    private lateinit var binding: ActivityMainBinding

    private val googleAccountProvider: GoogleAccountProvider by inject()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val selectedLanguage = LocaleHelper.getLocale(this)
        if (selectedLanguage != null) {
            setAppLocale(selectedLanguage)
        }
    }

    @SuppressWarnings("LongMethod", "NestedBlockDepth", "ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val selectedLanguage = LocaleHelper.getLocale(this)
        if (selectedLanguage != null) {
            setAppLocale(selectedLanguage)
        }

        mainViewModel.mappedFragmentLiveData().observe(this) { fragmentCarrier ->
            when {
                fragmentCarrier.shouldOpenInStaticSheet -> {
                    supportFragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.static_bottom_sheet, fragmentCarrier.fragment)
                        .commit()
                }
                fragmentCarrier.isBottomSheet -> {
                    (fragmentCarrier.fragment as? BottomSheetDialogFragment)
                        ?.show(supportFragmentManager, fragmentCarrier.fragment.tag)
                }
                fragmentCarrier.shouldBeAddedToBackStack -> {
                    supportFragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.main_fragment, fragmentCarrier.fragment)
                        .addToBackStack(fragmentCarrier.fragment.tag)
                        .commit()
                }
                else -> {
                    supportFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
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
        } else {
            Firebase.auth.uid?.let { uid ->
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.main_fragment,
                        HubFragment(HubConfig(getNameFromGoogleSignIn(), uid))
                    ).commit()
                intent.extras?.getString("type")?.let {
                    when (it) {
                        "MESSAGE" -> {
                            val guid = intent.extras?.getString("groupId")
                            val name = intent.extras?.getString("senderName")
                            val member = listOf(uid)
                            if (guid != null && name != null) {
                                val fragment = ChatFragment(ChatConfig(
                                    getNameFromGoogleSignIn(),
                                    uid,
                                    guid,
                                    name,
                                    uid,
                                    member,
                                    null)
                                )
                                supportFragmentManager
                                    .beginTransaction()
                                    .add(R.id.main_fragment, fragment)
                                    .addToBackStack(fragment.tag)
                                    .commit()
                            } else {
                                Unit
                            }
                        }
                        "FRIEND_REQUEST" -> {
                            val fragment = FriendRequestFragment(FriendRequestConfig(getNameFromGoogleSignIn(), uid))
                            supportFragmentManager
                                .beginTransaction()
                                .add(R.id.main_fragment, fragment)
                                .addToBackStack(fragment.tag)
                                .commit()
                        }
                        else -> {
                            Unit
                        }
                    }
                }
            }
            if (Firebase.auth.uid == null) {
                mainViewModel.postNavigation(LoginConfig())
            }
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

    fun getNameFromGoogleSignIn(): String =
        googleAccountProvider.tryGetAccount()?.let {
            (it.givenName ?: "") + " " + (it.familyName ?: "")
        } ?: ""

    fun setAppLocale(lang: String){
        LocaleHelper.setLocale(this, lang)
    }
}
