package com.dev.firedetector.data.model

data class DataAlatModel(
    val flameDetected: String?,
    val hum: Double?,
    val mqValue: String?,
    val temp: Double?,
    val timestamp: String?,
    val deviceId: String,
)
