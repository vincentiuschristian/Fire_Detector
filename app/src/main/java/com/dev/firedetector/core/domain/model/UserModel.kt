package com.dev.firedetector.core.domain.model

data class UserModel(
    val token: String,
    val isLogin: Boolean = false,
    val macAddresses: List<String> = emptyList()
)