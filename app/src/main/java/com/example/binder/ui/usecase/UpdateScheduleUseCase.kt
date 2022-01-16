package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import data.CalendarEvent
import repository.FirebaseRepository
import Result
import Result.Companion.loading

class UpdateScheduleUseCase(private val firebaseRepository: FirebaseRepository): BaseUseCase<CalendarEvent, Result<Void>>() {

    override val parameter: MutableLiveData<CalendarEvent> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(loading(data = null))
            emit(firebaseRepository.updateUserCalendarEvent(it))
        }
    }
}
