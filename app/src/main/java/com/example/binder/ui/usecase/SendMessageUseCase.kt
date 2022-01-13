package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import repository.RealtimeDB
import Result
import data.Message

class SendMessageUseCase<T: Pair<Message, String>>(private val realtimeDB: RealtimeDB) :
    BaseUseCase<T, Result<Void>>() {

    override val parameter: MutableLiveData<T> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(realtimeDB.sendMessage(it.first, it.second))
        }
    }
}