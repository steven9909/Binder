package com.example.binder.ui.usecase

import androidx.lifecycle.liveData
import Result
import repository.FirebaseRepository

class ApproveFriendRequestsUseCase(private val firebaseRepository: FirebaseRepository, requesterIds: List<String>) :
    BaseUseCase() {

    private val liveData = liveData {
        emit(Result.loading(null))
    }

    override fun getData() = liveData
}