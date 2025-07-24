package com.dev.firedetector.data.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("_id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("lokasi")
    val location: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("__v")
    val version: Int
)
