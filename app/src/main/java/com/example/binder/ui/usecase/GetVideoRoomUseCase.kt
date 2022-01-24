package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import Result
import repository.RoomIdRepository

class GetVideoRoomUseCase<T: Pair<String, String>>(private val roomIdRepository: RoomIdRepository) :
    BaseUseCase<T, Result<String>>() {
    override val parameter: MutableLiveData<T> = MutableLiveData()

    override val liveData: LiveData<Result<String>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(roomIdRepository.getRoomId(it.second, it.first))
        }
    }
}