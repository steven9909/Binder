package com.example.binder.ui.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HmsRoomIdApi {

    @Headers("Content-Type: application/json")
    @POST("/getRoomId")
    suspend fun getRoomId(@Body Request: RoomRequestBody) : RoomIdResponse

}