package com.example.binder.ui.usecase

import androidx.lifecycle.MutableLiveData
import data.CalendarEvent
import Result
import Result.Companion.loading
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class GetScheduleForUserUseCase(private val firebaseRepository: FirebaseRepository): BaseUseCase<Triple<String,Long,Long>,Result<List<CalendarEvent>>>() {

    override val parameter: MutableLiveData<Triple<String,Long,Long>> = MutableLiveData()

    override val liveData: LiveData<Result<List<CalendarEvent>>> = parameter.switchMap {
        liveData(Dispatchers.IO) {
            emit(loading(data = null))
            emit(firebaseRepository.getRelevantCalendarEventsForUser(it.first, it.second, it.third))
        }
    }
}