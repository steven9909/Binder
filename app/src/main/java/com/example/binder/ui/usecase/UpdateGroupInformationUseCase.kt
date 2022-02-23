package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import data.Group
import Result
import repository.FirebaseRepository

class UpdateGroupInformationUseCase(private val firebaseRepository: FirebaseRepository):
    BaseUseCase<Group, Result<Void>>() {

    override val parameter: MutableLiveData<Group> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
//            emit(firebaseRepository.updateBasicUserInformation(it))
        }
    }
}