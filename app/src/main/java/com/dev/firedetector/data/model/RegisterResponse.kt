package com.dev.firedetector.data.model

data class RegisterRequest(
    val device_id: String,
    val username: String,
    val email: String,
    val password: String,
    val location: String
)

data class RegisterResponse(
    val message: String,
    val error: String? = null
)