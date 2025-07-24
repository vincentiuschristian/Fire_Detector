package com.dev.firedetector.util

import android.app.Application
import android.util.Log

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MQTT_SERVICE", "MqttForegroundService Created")
    }
}


