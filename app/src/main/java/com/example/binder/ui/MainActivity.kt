package com.example.binder.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.binder.R
import data.LoginConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModel<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel.mappedFragmentLiveData().observe(this) { fragment ->
            Timber.d("fragment changed to $fragment")
            supportFragmentManager.beginTransaction().replace(R.id.main_fragment, fragment).commit()
        }
        mainViewModel.postNavigation(LoginConfig())
    }
}