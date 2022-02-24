package com.example.binder.ui.usecase

import Result
import Result.Companion.loading
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import data.CalendarEvent
import repository.FirebaseRepository

class DeleteScheduleUseCase(
    private val firebaseRepository: FirebaseRepository
    ): BaseUseCase<Pair<String, String>, Result<Void>>() {

    override val parameter: MutableLiveData<Pair<String, String>> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(loading(data = null))
            emit(firebaseRepository.deleteUserCalendarEventForUser(it.first, it.second))
        }
    }

}