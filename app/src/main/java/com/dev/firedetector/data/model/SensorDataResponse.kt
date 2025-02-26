package com.dev.firedetector.data.model

data class SensorDataResponse(
    val id: Int,
    val temperature: Float,
    val humidity: Float,
    val mq_status: String,
    val flame_status: String,
    val timestamp: String?
)