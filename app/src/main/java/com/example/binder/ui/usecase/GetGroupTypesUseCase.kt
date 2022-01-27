package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import repository.FirebaseRepository
import Result

class GetGroupTypesUseCase(private val firebaseRepository: FirebaseRepository) :
    BaseUseCase<String, Result<List<String>>>() {

    override val parameter: MutableLiveData<String> = MutableLiveData()

    override val liveData: LiveData<Result<List<String>>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.getSpecificGroupTypes(it))
        }
    }
}
