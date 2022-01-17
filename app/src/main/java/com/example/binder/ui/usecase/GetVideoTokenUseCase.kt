package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import Result
import repository.TokenRepository

class GetVideoTokenUseCase<T: Pair<String, String>>(private val tokenRepository: TokenRepository) :
    BaseUseCase<T, Result<String>>() {

    override val parameter: MutableLiveData<T> = MutableLiveData()

    override val liveData: LiveData<Result<String>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(tokenRepository.getToken(it.first, it.second))
        }
    }
}