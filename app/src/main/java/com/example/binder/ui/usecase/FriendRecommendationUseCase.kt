package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import Result
import com.example.binder.ui.api.UserApi
import repository.FriendRecommendationRepository

class FriendRecommendationUseCase(private val friendRecommendationRepository: FriendRecommendationRepository):
    BaseUseCase<String, Result<List<UserApi>>>() {

    override val parameter: MutableLiveData<String> = MutableLiveData()

    override val liveData: LiveData<Result<List<UserApi>>> = parameter.switchMap {
        liveData {
            emit(Result.loading(null))
            emit(friendRecommendationRepository.getRecommendationsFor(it))
        }
    }
}