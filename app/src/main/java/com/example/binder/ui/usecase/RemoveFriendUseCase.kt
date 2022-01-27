package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import repository.FirebaseRepository
import Result

class RemoveFriendUseCase(private val firebaseRepository: FirebaseRepository) :
    BaseUseCase<Pair<String, String>, Result<Void>>() {

    override val parameter: MutableLiveData<Pair<String, String>> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.removeUserFriend(it.first, it.second))
        }
    }
}
