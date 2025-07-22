package com.dev.firedetector.data.response

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("role")
    val role: String
)

data class RegisterResponse(
    @SerializedName("message")
    val message: String
)
