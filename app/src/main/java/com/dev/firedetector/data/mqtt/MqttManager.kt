package com.dev.firedetector.data.mqtt

object MqttManager {
    val mqttClientHelper: MqttClientHelper by lazy {
        MqttClientHelper()
    }
}
