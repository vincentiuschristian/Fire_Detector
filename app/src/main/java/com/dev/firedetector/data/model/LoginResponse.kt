package com.dev.firedetector.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val username: String? = null,
    val device_id: String? = null,
    val error: String? = null
)