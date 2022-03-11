package com.example.binder.ui.api

import com.google.gson.annotations.SerializedName

data class RecordingResponseBody(
    @SerializedName("roomName") val roomName: String,
    @SerializedName("arrayList") val arrayList: List<String>
)
