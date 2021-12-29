package com.example.binder.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.binder.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModel<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel.mappedFragmentLiveData().observe(this) {

        }
    }

    override fun onStart() {
        super.onStart()
    }
}