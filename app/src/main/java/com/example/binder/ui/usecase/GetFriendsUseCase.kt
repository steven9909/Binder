package com.example.binder.ui.usecase

import androidx.lifecycle.liveData
import repository.FirebaseRepository
import Result

class GetFriendsUseCase(private val firebaseRepository: FirebaseRepository): BaseUseCase() {

    private val liveData = liveData {
        emit(Result.loading(null))
        emit(firebaseRepository.getAdvancedUserFriends())
    }

    override fun getData() = liveData
}
