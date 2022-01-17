package repository

import com.example.binder.ui.api.HmsAuthTokenApi
import com.example.binder.ui.api.TokenRequestBody
import resultCatching
import Result
import okhttp3.internal.wait
import retrofit2.Retrofit

class TokenRepository(private val retrofit: Retrofit) {

    suspend fun getToken(uuid : String, roomId: String): Result<String> {
        return try {
            val tokenReq = TokenRequestBody(roomId = roomId, uuid = uuid)
            val token = retrofit
                .create(HmsAuthTokenApi::class.java)
                .getAuthToken(tokenReq).token
            Result.success(token)
        } catch(e: Exception) {
            Result.error(data = null, e)
        }
    }

}