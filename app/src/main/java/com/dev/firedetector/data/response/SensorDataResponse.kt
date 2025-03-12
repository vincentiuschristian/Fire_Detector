package com.dev.firedetector.data.response

import com.google.gson.annotations.SerializedName

data class SensorDataResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("temperature")
    val temperature: Float,

    @SerializedName("humidity")
    val humidity: Float,
    @SerializedName("mq_status")
    val mqStatus: String,

    @SerializedName("flame_status")
    val flameStatus: String,

    @SerializedName("timestamp")
    val timestamp: String?

)
