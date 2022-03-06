package repository

import retrofit2.Retrofit
import Result
import com.example.binder.ui.api.FriendRecommendApi
import com.example.binder.ui.api.UserApi

class FriendRecommendationRepository(private val retrofit: Retrofit) {
    suspend fun getRecommendationsFor(userId: String): Result<List<UserApi>> {
        return try {
            val response = retrofit
                .create(FriendRecommendApi::class.java)
                .getRecommendations(userId)
            Result.success(response.recommendation)
        } catch(e: Exception) {
            Result.error(data = null, e)
        }
    }
}