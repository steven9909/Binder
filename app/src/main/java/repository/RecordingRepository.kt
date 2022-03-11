package repository

import com.example.binder.ui.api.HmsAuthTokenApi
import com.example.binder.ui.api.HmsRecordingApi
import com.example.binder.ui.api.RecordingRequestBody
import com.example.binder.ui.api.TokenRequestBody
import retrofit2.Retrofit
import Result

class RecordingRepository(private val retrofit: Retrofit) {
    suspend fun getRecording(roomName: String): Result<List<String>> {
        return try {
            val recordRec = RecordingRequestBody(room_name = roomName)
            val recording = retrofit
                .create(HmsRecordingApi::class.java)
                .getRecording(recordRec).arrayList
            Result.success(recording)
        } catch(e: Exception) {
            Result.error(data = null, e)
        }
    }
}