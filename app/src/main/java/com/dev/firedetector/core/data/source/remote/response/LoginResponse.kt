package com.dev.firedetector.core.data.source.remote.response

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

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("mac_address")
    val macAddress: List<String>? = null
)