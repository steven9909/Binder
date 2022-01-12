package com.example.binder.ui.usecase

import androidx.lifecycle.liveData
import repository.FirebaseRepository
import Result

class GetFriendRequestsUseCase(private val firebaseRepository: FirebaseRepository): BaseUseCase() {

    private val liveData = liveData {
        emit(Result.loading(null))
        emit(firebaseRepository.getUserFriendRequests())
    }

    override fun getData() = liveData

}