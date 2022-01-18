package com.example.binder.ui.usecase

import androidx.lifecycle.liveData
import repository.FirebaseRepository
import Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import data.Group

class CreateGroupUseCase(val firebaseRepository: FirebaseRepository):
    BaseUseCase<Group, Result<Void>>() {

    override val parameter: MutableLiveData<Group> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.createGroup(it))
        }
    }
}
