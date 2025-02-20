package com.dev.firedetector.util

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.dev.firedetector.R

object Reference {
    const val COLLECTION = "Fire"
    const val DATAUSER = "DataUser"
    const val DATAALAT = "DataAlat"
    const val FIELD_TEMP = "Temperature"
    const val FIELD_HUM = "Humidity"
    const val FIELD_GAS_LEVEL = "MQValue"
    const val FIELD_FLAME_DETECTED = "FlameDetected"
    const val FIELD_TIMESTAMP = "timestamp"

    fun isEmailValid(context: Context, email: String): Boolean {
        return if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, R.string.email_warning, Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    fun isPasswordValid(context: Context, password: String): Boolean {
        return if (TextUtils.isEmpty(password) || password.length < 6) {
            Toast.makeText(context, R.string.password_warning, Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }
}

sealed class MqttConnectionState {
    data object Connected : MqttConnectionState()
    data object Disconnected : MqttConnectionState()
    data class Error(val exception: Throwable?) : MqttConnectionState()
}

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}