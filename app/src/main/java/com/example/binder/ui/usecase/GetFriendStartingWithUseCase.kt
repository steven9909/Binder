package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import data.User
import Result
import androidx.lifecycle.switchMap
import repository.FirebaseRepository

class GetFriendStartingWithUseCase<S: String>(private val firebaseRepository: FirebaseRepository) :
    BaseUseCase<S, Result<List<User>>>() {

    override val parameter: MutableLiveData<S> = MutableLiveData()

    override val liveData: LiveData<Result<List<User>>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
//            emit(firebaseRepository.searchFriendsWithName(it))
        }
    }
}