package com.dev.firedetector.data.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

    @SerializedName("device_id")
    val deviceId: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("location")
    val location: String,
)