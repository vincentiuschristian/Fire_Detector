package com.dev.firedetector.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.dev.firedetector.R
import com.dev.firedetector.data.mqtt.MqttClientHelper
import com.dev.firedetector.data.mqtt.MqttManager
import com.dev.firedetector.data.response.SensorDataResponse

class MqttForegroundService : Service() {

    private lateinit var mqttClientHelper: MqttClientHelper
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        notificationHelper = NotificationHelper(applicationContext)

        mqttClientHelper = MqttManager.mqttClientHelper
        mqttClientHelper.sensorLiveData.observeForever(sensorObserver)

        Log.d("MqttService", "Service created & observer registered")
    }


    private fun startForegroundService() {
        val channelId = "mqtt_foreground_channel"
        val channelName = "MQTT Background Service"

        val channel = NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Fire Detector Aktif")
            .setContentText("Memantau kebakaran di latar belakang")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(101, notification)
    }

    private val sensorObserver = Observer<SensorDataResponse> { data ->
        data.let {
            notificationHelper.handleIncomingSensorData(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttClientHelper.disconnect()
        mqttClientHelper.sensorLiveData.removeObserver(sensorObserver)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}


