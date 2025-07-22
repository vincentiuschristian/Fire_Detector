package com.dev.firedetector.data.response

data class UserResponse(
    val status: Int,
    val message: String,
    val data: UserData
)

data class UserData(
    val email: String,
    val username: String,
    val lokasi: String,
    val role: String
)
