package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import data.User
import Result
import repository.FirebaseRepository

class GetGroupInformationUseCase (private val firebaseRepository: FirebaseRepository) :
    BaseUseCase<Any, Result<User>>() {

    override val parameter: MutableLiveData<Any>? = null

    override val liveData: LiveData<Result<User>> = liveData {
        emit(Result.loading(null))
//        emit(firebaseRepository.getBasicUserInformation())
    }
}