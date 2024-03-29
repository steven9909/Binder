package com.example.binder.ui.usecase

import androidx.lifecycle.liveData
import repository.FirebaseRepository
import Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import data.User

class GetFriendRequestsUseCase(
    private val firebaseRepository: FirebaseRepository
): BaseUseCase<Any, Result<List<Pair<User, String?>>>>() {

    override val parameter: MutableLiveData<Any>? = null

    override val liveData: LiveData<Result<List<Pair<User, String?>>>> = liveData {
        emit(Result.loading(null))
        emit(firebaseRepository.getUserFriendRequests())
    }
}
