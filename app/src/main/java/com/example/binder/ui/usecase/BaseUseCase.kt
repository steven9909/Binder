package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class BaseUseCase<T, R> {
    abstract val parameter: MutableLiveData<T>?
    abstract val liveData: LiveData<R>
    fun getData() = liveData

    fun setParameter(t: T?) {
        parameter?.postValue(t)
    }

    fun refresh() {
        parameter?.postValue(parameter?.value)
    }
}
