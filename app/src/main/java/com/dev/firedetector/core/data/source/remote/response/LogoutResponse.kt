package com.dev.firedetector.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("message")
    val message: String
)