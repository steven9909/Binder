package com.example.binder.ui.api

import com.google.gson.annotations.SerializedName
import java.util.*

data class TokenResponse(
    @SerializedName("token") val token: String,
    @SerializedName("uuid") val uuid: String
)
