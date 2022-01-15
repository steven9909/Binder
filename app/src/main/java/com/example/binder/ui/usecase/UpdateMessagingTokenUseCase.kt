package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import data.Message
import Result
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import repository.FirebaseRepository

class UpdateMessagingTokenUseCase(private val firebaseRepository: FirebaseRepository, private val firebaseMessaging: FirebaseMessaging) :
    BaseUseCase<Any, Result<Void>>() {

    override val parameter: MutableLiveData<Any>? = null

    override val liveData: LiveData<Result<Void>> = liveData {
        emit(Result.loading(null))
        val token = firebaseMessaging.token.await()
        emit(firebaseRepository.updateToken(token))
    }
}