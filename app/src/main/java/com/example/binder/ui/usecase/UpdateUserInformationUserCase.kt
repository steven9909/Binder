package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import data.User
import Result
import androidx.lifecycle.switchMap
import repository.FirebaseRepository

class UpdateUserInformationUserCase(private val firebaseRepository: FirebaseRepository):
    BaseUseCase<User, Result<Void>>() {

    override val parameter: MutableLiveData<User> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.updateBasicUserInformation(it))
        }
    }
}
