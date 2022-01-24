package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import Result
import data.Question
import repository.FirebaseRepository

class AddQuestionToDBUseCase(private val firebaseRepository: FirebaseRepository) :
    BaseUseCase<Question, Result<Question>>() {

    override val parameter: MutableLiveData<Question> = MutableLiveData()

    override val liveData: LiveData<Result<Question>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(firebaseRepository.addQuestionToDB(it))
        }
    }
}