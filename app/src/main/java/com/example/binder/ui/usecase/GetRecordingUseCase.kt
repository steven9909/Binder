package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import repository.RecordingRepository
import Result
import repository.TokenRepository

    class GetRecordingUseCase<T: String>(private val RecordingRepository: RecordingRepository) :
    BaseUseCase<T, Result<List<String>>>() {

    override val parameter: MutableLiveData<T> = MutableLiveData()

    override val liveData: LiveData<Result<List<String>>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(RecordingRepository.getRecording(it))
        }
    }
}