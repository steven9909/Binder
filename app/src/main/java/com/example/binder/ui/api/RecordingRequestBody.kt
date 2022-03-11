package com.example.binder.ui.api

import com.google.gson.annotations.SerializedName

data class RecordingRequestBody(
    @SerializedName("room_name") val roomName: String
)
