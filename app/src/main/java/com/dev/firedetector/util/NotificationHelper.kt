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
import com.dev.firedetector.data.pref.UserPreference
import com.google.firebase.firestore.FirebaseFirestore

class NotificationHelper(
    private val context: Context,
    private val userPreference: UserPreference
) {

    private val CHANNEL_ID = "fire_alert_channel"
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val lastFlameStatus: MutableMap<String, String> = mutableMapOf()
    private val notificationReceiver = NotificationReceiver()

    fun registerNotificationReceiver() {
        val filter = IntentFilter("STOP_NOTIFICATION")
        ContextCompat.registerReceiver(
            context, // ⬅️ Gunakan context yang diberikan dari MainActivity
            notificationReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    fun unregisterNotificationReceiver() {
        context.unregisterReceiver(notificationReceiver) // ✅ Unregister receiver
    }


    fun createNotificationChannel() {
        val name = "Fire Alert Channel"
        val descriptionText = "Channel for fire alert notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendFireAlertNotification(title: String, message: String, deviceId: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/alarm")

        val stopIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = "STOP_NOTIFICATION"
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            deviceId.hashCode(),
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "OK", stopPendingIntent)
            .build()

        notificationManager.notify(deviceId.hashCode(), notification)
    }

    class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent?.action == "STOP_NOTIFICATION") {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(1)
            }
        }
    }

    fun startListening() {
        // Ambil deviceId dari datastore
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val deviceId = userPreference.getSession().first().idPerangkat
//
//                db.collection(Reference.COLLECTION)
//                    .document(deviceId)
//                    .collection(Reference.DATAALAT)
//                    .orderBy("timestamp", Query.Direction.DESCENDING)
//                    .limit(1)
//                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                        if (firebaseFirestoreException != null) {
//                            Log.e("Firestore", "Error: ${firebaseFirestoreException.message}")
//                            return@addSnapshotListener
//                        }
//
//                        querySnapshot?.let { snapshot ->
//                            for (document in snapshot.documents) {
//                                val flameDetected =
//                                    document.getString(Reference.FIELD_FLAME_DETECTED)
//
//                                flameDetected?.let { data ->
//                                    val documentKey = "${deviceId}_${document.id}"
//
//                                    if (lastFlameStatus[documentKey] != data) {
//                                        lastFlameStatus[documentKey] = data
//
//                                        if (data == "Api Terdeteksi") {
//                                            sendFireAlertNotification(
//                                                "Api Terdeteksi!",
//                                                "Api terdeteksi! Segera Periksa Ruangan Anda",
//                                                deviceId
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//            } catch (e: Exception) {
//                Log.e("startListening", "Error fetching deviceId: ${e.message}")
//            }
//        }
    }

    fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                (context as? MainActivity)?.requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            } else {
                Log.d("Permission", "Notification permission granted")
            }
        } else {
            Log.d("Permission", "Notification permission not required for this API level")
        }
    }
}