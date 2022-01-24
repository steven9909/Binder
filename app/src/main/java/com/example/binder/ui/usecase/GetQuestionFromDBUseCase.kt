package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import data.Question
import repository.FirebaseRepository
import Result

class GetQuestionFromDBUseCase(private val firebaseRepository: FirebaseRepository) :
BaseUseCase<String, Result<Question>>() {

    override val parameter: MutableLiveData<String> = MutableLiveData()

    override val liveData: LiveData<Result<Question>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.getQuestionFromDB(it))
        }
    }
}