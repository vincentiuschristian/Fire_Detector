package com.dev.firedetector.data.response

import com.google.gson.annotations.SerializedName

data class DeviceLocationResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("device_number")
    val deviceNumber: Int,

    @SerializedName("zone_name")
    val zoneName: String,

    @SerializedName("device_location")
    val deviceLocation: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String
)


data class DeviceLocationUpdate(
    @SerializedName("device_number")
    val deviceNumber: Int,

    @SerializedName("zone_name")
    val zoneName: String,

    @SerializedName("device_location")
    val deviceLocation: String?
)


data class GenericResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("updated_devices")
    val updatedDevices: Int?
)

