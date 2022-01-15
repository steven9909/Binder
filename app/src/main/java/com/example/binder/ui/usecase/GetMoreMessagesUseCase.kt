package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import repository.RealtimeDB
import Result
import data.Message

class GetMoreMessagesUseCase<T: Pair<String, Long>>(private val realtimeDB: RealtimeDB) : BaseUseCase<T, Result<List<Message>>>() {

    override val parameter: MutableLiveData<T> = MutableLiveData()

    override val liveData: LiveData<Result<List<Message>>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(realtimeDB.getMoreMessages(it.first, it.second))
        }
    }
}