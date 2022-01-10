package com.example.binder.ui.usecase

import androidx.lifecycle.liveData
import repository.FirebaseRepository
import Result

class GetFriendsUseCase(val firebaseRepository: FirebaseRepository): BaseUseCase() {

    private val liveData = liveData {
        emit(Result.loading(null))
        emit(firebaseRepository.getBasicUserFriends())
    }

    override fun getData() = liveData
}
