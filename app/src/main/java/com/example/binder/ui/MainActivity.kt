package com.example.binder.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.example.binder.R
import com.example.binder.databinding.ActivityMainBinding
import data.LoginConfig
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModel<MainActivityViewModel>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mainViewModel.mappedFragmentLiveData().observe(this) { fragmentCarrier ->
            Timber.d("fragment changed to ${fragmentCarrier.fragment}, backstack: ${fragmentCarrier.shouldBeAddedToBackStack}")
            if (fragmentCarrier.shouldBeAddedToBackStack) {
                supportFragmentManager.beginTransaction().add(R.id.main_fragment, fragmentCarrier.fragment).addToBackStack(fragmentCarrier.fragment.tag).commit()
            } else {
                supportFragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentCarrier.fragment).commit()
            }
        }
        mainViewModel.getLoadingLiveData().observe(this) { isLoading ->
            binding.loadingScreen.isVisible = !isLoading
        }
        mainViewModel.postNavigation(LoginConfig())
    }
}