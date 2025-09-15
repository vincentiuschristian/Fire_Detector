package com.dev.firedetector.core.data.source.mqtt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev.firedetector.core.data.source.remote.response.SensorDataResponse
import com.google.gson.Gson
import org.eclipse.paho.mqttv5.client.IMqttToken
import org.eclipse.paho.mqttv5.client.MqttAsyncClient
import org.eclipse.paho.mqttv5.client.MqttCallback
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse
import org.eclipse.paho.mqttv5.common.MqttException
import org.eclipse.paho.mqttv5.common.MqttMessage
import org.eclipse.paho.mqttv5.common.packet.MqttProperties
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttClientHelper @Inject constructor() {
    private val serverUri = "ssl://d7d8ee83.ala.asia-southeast1.emqxsl.com:8883"
    private val clientId = "android_client_${System.currentTimeMillis()}"
    private val mqttClient = MqttAsyncClient(serverUri, clientId, null)
    private var pendingMacList: List<String> = emptyList()

    private val _sensorLiveData = MutableLiveData<SensorDataResponse>()
    val sensorLiveData: LiveData<SensorDataResponse> get() = _sensorLiveData

    fun connect() {
        val options = MqttConnectionOptions().apply {
            isCleanStart = true
            keepAliveInterval = 30
            userName = "firedetect"
            password = "123".toByteArray()
            socketFactory = SSLSocketFactoryHelper.getSocketFactory()
        }

        mqttClient.setCallback(object : MqttCallback {
            override fun disconnected(disconnectResponse: MqttDisconnectResponse) {
                Log.d("MQTT", "Disconnected: ${disconnectResponse.reasonString}")
            }

            override fun mqttErrorOccurred(exception: MqttException) {
                Log.e("MQTT", "MQTT Error: ${exception.message}")
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.d("MQTT", "Message received: $message on topic: $topic")
                try {
                    val sensorData =
                        Gson().fromJson(message.toString(), SensorDataResponse::class.java)
                    Log.d("MQTT", "Parsed SensorData: $sensorData")
                    _sensorLiveData.postValue(sensorData)
                } catch (e: Exception) {
                    Log.e("MQTT", "Failed to parse MQTT message: ${e.message}")
                }
            }

            override fun deliveryComplete(token: IMqttToken) {}
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                Log.d("MQTT", "Connected to $serverURI")
                if (pendingMacList.isNotEmpty()) {
                    subscribeTopics(pendingMacList)
                    pendingMacList = emptyList()
                }
            }

            override fun authPacketArrived(reasonCode: Int, properties: MqttProperties) {}
        })

        mqttClient.connect(options)
    }

    fun subscribeTopics(macList: List<String>) {
        if (!mqttClient.isConnected) {
            Log.w("MQTT", "MQTT not connected yet, deferring subscription.")
            pendingMacList = macList
            return
        }
        macList.forEach { mac ->
            val topic = "fire_detector/$mac"
            try {
                mqttClient.subscribe(topic, 1)
                Log.d("MQTT", "Subscribed to topic: $topic")
            } catch (e: MqttException) {
                Log.e("MQTT", "Failed to subscribe to $topic: ${e.message}")
            }
        }
    }

    fun disconnect() {
        if (mqttClient.isConnected) {
            mqttClient.disconnect()
        }
    }

}
