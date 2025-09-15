package com.dev.firedetector.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("lokasi")
    val location: String,

    @SerializedName("role")
    val role: String
)

data class RegisterResponse(
    @SerializedName("message")
    val message: String
)
