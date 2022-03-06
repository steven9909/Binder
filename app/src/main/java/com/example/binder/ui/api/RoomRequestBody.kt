package com.example.binder.ui.api

import com.google.gson.annotations.SerializedName

data class RoomRequestBody(
    @SerializedName("groupId") val groupId: String,
    @SerializedName("uuid") val uuid: String
)
