package com.example.binder.ui.usecase

import androidx.lifecycle.liveData
import repository.FirebaseRepository
import Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import data.Group

class GetGroupsUseCase(private val firebaseRepository: FirebaseRepository) : BaseUseCase<Any, Result<List<Group>>>() {

    override val parameter: MutableLiveData<Any>? = null

    override val liveData: LiveData<Result<List<Group>>> = liveData {
        emit(Result.loading(null))
        emit(firebaseRepository.getAllUserGroups())
    }
}
