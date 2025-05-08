package com.dev.firedetector.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import kotlinx.coroutines.launch

class NotificationHelper(
    private val context: Context,
    private val repository: FireRepository
) {
    private val CHANNEL_ID = "fire_alert_channel"
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationReceiver = NotificationReceiver()

    private val lastStatusMap = mutableMapOf<String, Pair<String?, String?>>(
        "ruang_tamu" to Pair(null, null),
        "kamar" to Pair(null, null)
    )

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
        context.unregisterReceiver(notificationReceiver)
    }

    fun createNotificationChannel() {
        val name = "Fire Alert Channel"
        val descriptionText = "Channel for fire and smoke alert notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 1000, 500, 1000)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun startListening() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                checkBothLocations()
                delay(2000)
            }
        }
    }

    private suspend fun checkBothLocations() {
        checkLocation("ruang_tamu", "Ruang Tamu")
        checkLocation("kamar", "Kamar")
    }

    private suspend fun checkLocation(locationKey: String, locationName: String) {
        try {
            val result = when (locationKey) {
                "ruang_tamu" -> repository.getLatestDataZona1()
                "kamar" -> repository.getLatestDataZona2()
                else -> return
            }

            if (result is Result.Success) {
                handleSensorData(result.data, locationKey, locationName)
            } else if (result is Result.Error) {
                Log.e("Notification", "Error fetching $locationName data: ${result.error}")
            }
        } catch (e: Exception) {
            Log.e("Notification", "Error checking $locationName: ${e.message}")
        }
    }

    private fun handleSensorData(data: SensorDataResponse, locationKey: String, locationName: String) {
        val currentFlame = data.flameStatus
        val currentMQ = data.mqStatus
        val lastStatus = lastStatusMap[locationKey] ?: Pair(null, null)

        if (currentFlame == "terdeteksi" && lastStatus.first != "terdeteksi") {
            sendAlertNotification(
                title = "Api Terdeteksi!",
                message = "Api terdeteksi di $locationName! Segera evakuasi!",
                notificationId = locationKey.hashCode() + 1,
                isFire = true
            )
        }

        if (currentMQ == "terdeteksi" && lastStatus.second != "terdeteksi") {
            sendAlertNotification(
                title = "Asap Terdeteksi!",
                message = "Asap terdeteksi di $locationName! Periksa sumbernya!",
                notificationId = locationKey.hashCode() + 2,
                isFire = false
            )
        }

        lastStatusMap[locationKey] = Pair(currentFlame, currentMQ)
    }

    private fun sendAlertNotification(title: String, message: String, notificationId: Int, isFire: Boolean) {
        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/alarm")

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

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(if (isFire) R.drawable.icon_fire else R.drawable.icon_gas)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .addAction(
                R.drawable.ic_close,
                context.getString(R.string.supporting_text),
                stopPendingIntent
            )
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                (context as? MainActivity)?.requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
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