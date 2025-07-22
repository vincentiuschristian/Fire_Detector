package com.dev.firedetector.util

import android.app.Application
import android.content.Intent

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val intent = Intent(this, MqttForegroundService::class.java)
        startForegroundService(intent)
    }
}

