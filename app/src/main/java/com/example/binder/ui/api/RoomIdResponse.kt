package com.example.binder.ui.api

import com.google.gson.annotations.SerializedName

data class RoomIdResponse(
    @SerializedName("roomId") val roomId: String,
    @SerializedName("uuid") val uuid: String
)
