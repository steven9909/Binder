package com.example.binder.ui.api

import com.google.gson.annotations.SerializedName

data class RecommendationResponse(
    @SerializedName("recommendations") val recommendation: List<UserApi>
)

data class UserApi(
    @SerializedName("school") val school:String?,
    @SerializedName("program") val program:String?,
    @SerializedName("interests") val interests:List<String>?,
    @SerializedName("name") val name:String?=null,
    @SerializedName("token") val token:String?=null,
    @SerializedName("userGroups") val userGroups:List<String>,
    @SerializedName("score") val score:String,
    @SerializedName("user_id") val userId: String
)