package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import data.CalendarEvent
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository
import Result
import Result.Companion.loading

class GetAllGroupScheduleUseCase (private val firebaseRepository: FirebaseRepository
): BaseUseCase<Triple<String,Long,Long>,Result<List<CalendarEvent>>>() {

    override val parameter: MutableLiveData<Triple<String, Long, Long>> = MutableLiveData()

    override val liveData: LiveData<Result<List<CalendarEvent>>> = parameter.switchMap {
        liveData(Dispatchers.IO) {
            emit(loading(data = null))
            val group = firebaseRepository.getAllUserGroups()
            if (group.status == Status.SUCCESS) {

            } else {
                if (group.exception != null) {
                    emit(Result.error(null, group.exception))
                } else {
                    emit(Result.error(null, GroupIDsNotFoundException))
                }
            }
        }
    }

}

object GroupIDsNotFoundException: Exception()