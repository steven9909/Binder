package com.example.binder.ui.api

import java.util.*
import com.google.gson.annotations.SerializedName

data class TokenRequestBody(
    @SerializedName("roomId") val roomId: String,
    @SerializedName("uuid") val uuid: String
)
