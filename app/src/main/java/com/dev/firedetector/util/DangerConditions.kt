package com.dev.firedetector.util

data class DangerConditions(
    val isHighTemperature: Boolean,
    val isGasDetected: Boolean,
    val isFireDetected: Boolean
) {
    companion object {
        fun fromSensorData(
            temperature: Float,
            mqStatus: String,
            flameStatus: String
        ): DangerConditions {
            return DangerConditions(
                isHighTemperature = temperature > 45,
                isGasDetected = mqStatus == "Terdeteksi",
                isFireDetected = flameStatus == "Api Terdeteksi"
            )
        }
    }
}