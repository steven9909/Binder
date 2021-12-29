package com.example.binder.ui

import androidx.fragment.app.Fragment
import data.Config

abstract class BaseFragment: Fragment() {
    abstract val config: Config
}