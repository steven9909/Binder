package com.example.binder.ui.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface HmsAuthTokenApi {

    @Headers("Content-Type: application/json")
    @POST("/getToken")
    suspend fun getAuthToken(@Body Request: TokenRequestBody) : TokenResponse

}