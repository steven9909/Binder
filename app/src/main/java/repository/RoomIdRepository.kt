package repository

import com.example.binder.ui.api.HmsRoomIdApi
import com.example.binder.ui.api.RoomRequestBody
import Result
import retrofit2.Retrofit

@SuppressWarnings("TooGenericExceptionCaught")
class RoomIdRepository(private val retrofit: Retrofit) {
    suspend fun getRoomId(uuid : String, groupId: String): Result<String> {
        return try {
            val roomReq = RoomRequestBody(groupId = groupId, uuid = uuid)
            val roomId = retrofit
                .create(HmsRoomIdApi::class.java)
                .getRoomId(roomReq).roomId
            Result.success(roomId)
        } catch(e: Exception) {
            Result.error(data = null, e)
        }
    }
}
