package com.example.binder.ui.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HmsRecordingApi {
    @Headers("Content-Type: application/json")
    @POST("/getRecordings")
    suspend fun getRecording(@Body request: RecordingRequestBody) : RecordingResponseBody
}
