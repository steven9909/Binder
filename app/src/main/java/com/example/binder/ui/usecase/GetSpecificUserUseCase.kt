package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import Result
import androidx.lifecycle.liveData
import data.User
import repository.FirebaseRepository

class GetSpecificUserUseCase(private val firebaseRepository: FirebaseRepository) :
    BaseUseCase<String, Result<User>>() {

    override val parameter: MutableLiveData<String> = MutableLiveData()

    override val liveData: LiveData<Result<User>> = parameter.switchMap{
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.getSpecificUserInformation(it))
        }
    }
}
