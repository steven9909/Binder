package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import repository.FirebaseRepository
import Result

class CreateGoogleDriveFolderUseCase<T: List<Pair<String, String>>>(private val firebaseRepository: FirebaseRepository) :
    BaseUseCase<T, Result<Void>>() {

    override val parameter: MutableLiveData<T> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.addFriendDeleteFriendRequests(it))
        }
    }
}