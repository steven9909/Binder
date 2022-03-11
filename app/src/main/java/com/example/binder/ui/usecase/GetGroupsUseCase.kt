package com.example.binder.ui.usecase

import androidx.lifecycle.liveData
import repository.FirebaseRepository
import Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import data.BaseData
import data.Group
import data.User
import kotlinx.coroutines.Dispatchers

class GetGroupsUseCase(private val firebaseRepository: FirebaseRepository) :
    BaseUseCase<Any, Result<MutableList<Pair<User?, Group>>>>() {

    override val parameter: MutableLiveData<Any> = MutableLiveData<Any>()

    override val liveData: LiveData<Result<MutableList<Pair<User?, Group>>>> = parameter.switchMap {
       liveData(Dispatchers.IO) {
           emit(Result.loading(null))
           emit(firebaseRepository.getAllUserGroups())
       }
    }
}
