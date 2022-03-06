package com.example.binder.ui.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendRecommendApi {
    @GET("/api/recommendations")
    suspend fun getRecommendations(@Query("user_id") userId: String): RecommendationResponse
}
