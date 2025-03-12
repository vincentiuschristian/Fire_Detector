package com.dev.firedetector.data.response

data class UserResponse(
    val id: Int,
    val deviceId: String,
    val username: String,
    val email: String,
    val location: String,
)