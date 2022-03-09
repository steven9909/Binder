package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import Result
import data.CalendarEvent
import repository.FirebaseRepository

class BatchCalendarEventUpdateUseCase(val firebaseRepository: FirebaseRepository):
    BaseUseCase<Pair<String,List<CalendarEvent>>, Result<Void>>() {

    override val parameter: MutableLiveData<Pair<String,List<CalendarEvent>>> = MutableLiveData()

    override val liveData: LiveData<Result<Void>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.batchUpdateUserCalendarEvent(it.first, it.second))
        }
    }
}
