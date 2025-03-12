package com.dev.firedetector.data.response

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String

)

data class LoginResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("device_id")
    val deviceId: String? = null,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("error")
    val error: String? = null
)
