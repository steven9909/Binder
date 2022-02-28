package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import data.User
import Result
import repository.FirebaseRepository

class DoesUserExistUseCase(private val firebaseRepository: FirebaseRepository
): BaseUseCase<Any, Result<Boolean>>() {

    override val parameter: MutableLiveData<Any>? = null

    override val liveData: LiveData<Result<Boolean>> = liveData {
        emit(Result.loading(null))
        emit(firebaseRepository.doesUserExist())
    }

}