package com.example.binder.ui.usecase

import androidx.lifecycle.liveData
import Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import repository.FirebaseRepository

class ApproveFriendRequestsUseCase(private val firebaseRepository: FirebaseRepository) :
    BaseUseCase<List<Pair<String, String>>, Result<Void>>() {

    override val parameter: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.addFriendDeleteFriendRequests(it))
        }
    }
}
