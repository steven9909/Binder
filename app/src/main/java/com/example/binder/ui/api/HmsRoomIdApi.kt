package com.example.binder.ui.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

@SuppressWarnings("FunctionParameterNaming")
interface HmsRoomIdApi {

    @Headers("Content-Type: application/json")
    @POST("/getRoomId")
    suspend fun getRoomId(@Body Request: RoomRequestBody) : RoomIdResponse

}
