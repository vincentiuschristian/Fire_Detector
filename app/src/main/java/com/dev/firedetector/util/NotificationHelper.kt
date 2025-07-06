package com.dev.firedetector.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dev.firedetector.MainActivity
import com.dev.firedetector.R
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.data.response.SensorDataResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NotificationHelper(
    private val context: Context,
    private val repository: FireRepository
) {
    private val CHANNEL_ID = "fire_alert_channel"
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationReceiver = NotificationReceiver()

    private val lastStatusMap = mutableMapOf<String, Triple<String?, String?, Float?>>(
        "device_1" to Triple(null, null, null),
        "device_2" to Triple(null, null, null)
    )

    private val lastNotificationTimeMap = mutableMapOf<Int, Long>()
    private val notificationCooldown = 5000L

    private val zoneNames = mutableMapOf(
        1 to "Ruang Tamu",
        2 to "Kamar"
    )

    private var lastZoneNameUpdate = 0L

    init {
        createNotificationChannel()
        registerNotificationReceiver()
    }

    fun registerNotificationReceiver() {
        val filter = IntentFilter("STOP_NOTIFICATION")
        ContextCompat.registerReceiver(
            context,
            notificationReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    fun unregisterNotificationReceiver() {
        try {
            context.unregisterReceiver(notificationReceiver)
        } catch (e: IllegalArgumentException) {
            Log.e("Notification", "Receiver not registered")
        }
    }

    fun createNotificationChannel() {
        notificationManager.deleteNotificationChannel(CHANNEL_ID)

        val name = "Fire Alert Channel"
        val descriptionText = "Channel for fire, gas, and temperature alerts"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.alarm_siren_sound}")

        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            setSound(soundUri, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build())
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun startListening() {
        CoroutineScope(Dispatchers.IO).launch {
            loadZoneNames()
            while (isActive) {
                checkBothLocations()
                delay(2000)
            }
        }
    }

    private suspend fun checkBothLocations() {
        updateZoneNamesIfNeeded()

        checkLocation("device_1", 1)
        checkLocation("device_2", 2)
    }

    private suspend fun updateZoneNamesIfNeeded() {
        val now = System.currentTimeMillis()
        if (now - lastZoneNameUpdate > 5 * 60 * 1000) {
            loadZoneNames()
            lastZoneNameUpdate = now
        }
    }

    private suspend fun loadZoneNames() {
        when (val result = repository.getDeviceLocations()) {
            is Result.Success -> {
                result.data.forEach { location ->
                    zoneNames[location.deviceNumber] = location.zoneName
                }
            }
            is Result.Error -> {
                Log.e("Notification", "Error loading zone names: ${result.error}")
            }
            else -> {}
        }
    }

    private suspend fun checkLocation(deviceKey: String, deviceNumber: Int) {
        try {
            val locationName = zoneNames[deviceNumber] ?: when(deviceNumber) {
                1 -> "Ruang Tamu"
                2 -> "Kamar"
                else -> "Unknown Location"
            }

            val result = when (deviceNumber) {
                1 -> repository.getLatestDataZona1()
                2 -> repository.getLatestDataZona2()
                else -> return
            }

            when (result) {
                is Result.Success -> handleSensorData(result.data, deviceKey, locationName)
                is Result.Error -> Log.e("Notification", "Error fetching device $deviceNumber data: ${result.error}")
                else -> {}
            }
        } catch (e: Exception) {
            Log.e("Notification", "Error checking device $deviceNumber: ${e.message}")
        }
    }

    private fun handleSensorData(data: SensorDataResponse, deviceKey: String, locationName: String) {
        val currentFlame = data.flameStatus
        val currentMQ = data.mqStatus
        val currentTemp = data.temperature

        if (currentFlame == "Api Terdeteksi") {
            sendAlertNotification(
                title = "Api Terdeteksi di $locationName!",
                message = "Terdeteksi api di $locationName! Segera cek ke lokasi!",
                notificationId = deviceKey.hashCode() + 1
            )
        }

        if (currentMQ == "Terdeteksi") {
            sendAlertNotification(
                title = "Terdeteksi Asap di $locationName!",
                message = "Segera cek kondisi di $locationName!",
                notificationId = deviceKey.hashCode() + 2
            )
        }

        if (currentTemp > 40) {
            sendAlertNotification(
                title = "Suhu Tinggi di $locationName!",
                message = "Suhu mencapai ${currentTemp}°C di $locationName!",
                notificationId = deviceKey.hashCode() + 3
            )
        }

        lastStatusMap[deviceKey] = Triple(currentFlame, currentMQ, currentTemp)
    }

    private fun sendAlertNotification(
        title: String,
        message: String,
        notificationId: Int
    ) {
        val currentTime = System.currentTimeMillis()
        val lastTime = lastNotificationTimeMap[notificationId] ?: 0
        if (currentTime - lastTime < notificationCooldown) {
            return
        }
        lastNotificationTimeMap[notificationId] = currentTime

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("Notification", "Notification permission not granted")
            return
        }

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = "STOP_NOTIFICATION"
            putExtra("NOTIFICATION_ID", notificationId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.alarm_siren_sound}")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setOnlyAlertOnce(false)
            .setContentIntent(contentPendingIntent)
            .addAction(
                R.drawable.ic_close,
                "Tutup",
                stopPendingIntent
            )
            .build()

        notificationManager.notify(notificationId, notification)
    }

    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent?.action == "STOP_NOTIFICATION") {
                val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
                if (notificationId != -1) {
                    notificationManager.cancel(notificationId)
                }
            }
        }
    }
}
