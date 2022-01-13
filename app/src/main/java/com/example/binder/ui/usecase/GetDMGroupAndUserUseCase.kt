package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import data.DMGroup
import data.User
import repository.FirebaseRepository
import Result

class GetDMGroupAndUserUseCase(private val firebaseRepository: FirebaseRepository): BaseUseCase<Any, Result<List<Pair<User, DMGroup>>>>() {

    override val parameter: MutableLiveData<Any>? = null

    override val liveData: LiveData<Result<List<Pair<User, DMGroup>>>> = liveData {
        emit(Result.loading(null))
        emit(firebaseRepository.getAdvancedDMGroup())
    }
}