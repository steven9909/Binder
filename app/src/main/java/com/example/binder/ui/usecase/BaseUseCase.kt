package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import Result

abstract class BaseUseCase {
    abstract fun getData(): Any
}