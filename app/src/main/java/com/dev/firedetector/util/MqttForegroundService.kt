package com.dev.firedetector.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.dev.firedetector.R
import com.dev.firedetector.core.data.source.remote.response.SensorDataResponse
import com.dev.firedetector.core.data.source.mqtt.MqttClientHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MqttForegroundService : Service() {

    @Inject lateinit var mqttClientHelper: MqttClientHelper
    @Inject lateinit var notificationHelper: NotificationHelper

    private val sensorObserver = Observer<SensorDataResponse> { data ->
        notificationHelper.handleIncomingSensorData(data)
    }

    override fun onCreate() {
        super.onCreate()

        mqttClientHelper.connect()

        startForegroundService()
        mqttClientHelper.sensorLiveData.observeForever(sensorObserver)
        Log.d("MqttService", "Service created & observer registered")
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttClientHelper.sensorLiveData.removeObserver(sensorObserver)
        mqttClientHelper.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundService() {
        val channelId = "mqtt_foreground_channel"
        val channelName = "MQTT Background Service"

        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            manager.getNotificationChannel(channelId) == null
        ) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            )
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Fire Detector Aktif")
            .setContentText("Memantau kebakaran di latar belakang")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(101, notification)
    }
}


