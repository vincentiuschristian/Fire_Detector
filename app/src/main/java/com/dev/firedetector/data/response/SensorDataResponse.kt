package com.dev.firedetector.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SensorDataResponse(
    @SerializedName("temperature")
    val temperature: Double,

    @SerializedName("humidity")
    val humidity: Double,

    @SerializedName("MQ")
    val mqStatus: String,

    @SerializedName("Flame")
    val flameStatus: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("mac_address")
    val macAddress: String,

    @SerializedName("timestamp")
    val timestamp: String
) : Parcelable
