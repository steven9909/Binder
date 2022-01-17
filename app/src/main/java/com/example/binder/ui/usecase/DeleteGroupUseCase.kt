package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import Result
import androidx.lifecycle.switchMap
import repository.FirebaseRepository

class DeleteGroupUseCase(val firebaseRepository: FirebaseRepository) :
    BaseUseCase<String, Result<Void>>() {

    override val parameter: MutableLiveData<String> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.deleteGroup(it))
        }
    }
}
