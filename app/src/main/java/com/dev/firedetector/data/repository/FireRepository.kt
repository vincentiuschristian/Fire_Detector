package com.dev.firedetector.data.repository

import android.content.Context
import android.util.Log
import com.dev.firedetector.data.model.DataAlatModel
import com.dev.firedetector.data.model.DataUserModel
import com.dev.firedetector.data.pref.IDPerangkatModel
import com.dev.firedetector.data.pref.UserPreference
import com.dev.firedetector.util.MqttConnectionState
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.UUID

class FireRepository(private val userPreference: UserPreference, context: Context) {
    private val appContext = context.applicationContext
    private val auth = FirebaseAuth.getInstance()

    // MQTT Configuration
    private val mqttClient: MqttAndroidClient
    private val _sensorData = MutableStateFlow<List<DataAlatModel>>(emptyList())
    val sensorData: StateFlow<List<DataAlatModel>> = _sensorData

    // MQTT State Management
    private val _mqttState = MutableStateFlow<MqttConnectionState>(MqttConnectionState.Disconnected)
    val mqttState: StateFlow<MqttConnectionState> = _mqttState

    init {
        val serverURI = "ssl://d7d8ee83.ala.asia-southeast1.emqxsl.com:8883"
        mqttClient = MqttAndroidClient(
            appContext,
            serverURI,
            "android_client_${UUID.randomUUID()}" // Client ID unik
        )
        setupMqttConnection()
    }

    private fun setupMqttConnection() {
        mqttClient.setCallback(object : MqttCallback {
            override  fun messageArrived(topic: String, message: MqttMessage) {
                handleIncomingMessage(topic, String(message.payload))
            }

            override fun connectionLost(cause: Throwable?) {
                _mqttState.value = MqttConnectionState.Disconnected
                Log.e(TAG, "MQTT Connection Lost: ${cause?.message}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(TAG, "Message delivery complete")
            }
        })
    }

    fun connectToMQTT() {
        val options = MqttConnectOptions().apply {
            isCleanSession = true
            isAutomaticReconnect = true
            connectionTimeout = 10
            keepAliveInterval = 60
            userName = "firedetect"
            password = "123".toCharArray()
        }

        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    _mqttState.value = MqttConnectionState.Connected
                    subscribeToTopic("fire_detector/data")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    _mqttState.value = MqttConnectionState.Error(exception)
                    Log.e(TAG, "MQTT Connection failed: ${exception?.message}")
                }
            })
        } catch (e: MqttException) {
            _mqttState.value = MqttConnectionState.Error(e)
            Log.e(TAG, "MQTT Exception: ${e.message}")
        }
    }

    private fun handleIncomingMessage(topic: String, payload: String) {
        try {
            val data = Gson().fromJson(payload, DataAlatModel::class.java)

            _sensorData.update { currentList ->
                listOf(data) + currentList.take(99) // Keep last 100 items
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing MQTT message: ${e.message}")
        }
    }

    fun subscribeToTopic(topic: String) {
        try {
            mqttClient.subscribe(topic, 1, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e(TAG, "Subscription failed: ${exception?.message}")
                }
            })
        } catch (e: MqttException) {
            Log.e(TAG, "Subscription error: ${e.message}")
        }
    }

    fun disconnectFromMQTT() {
        try {
            mqttClient.disconnect()
            _mqttState.value = MqttConnectionState.Disconnected
        } catch (e: MqttException) {
            Log.e(TAG, "Disconnection error: ${e.message}")
        }
    }

    fun getUser(): String? = auth.uid

    fun login(
        email: String, password: String,
        onSuccess: (AuthResult) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    fun register(
        dataUserModel: DataUserModel,
        email: String,
        password: String,
        onResult: (Boolean, Exception?) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val userId = it.user!!.uid
            dataUserModel.email = email
            dataUserModel.idPerangkat = userId
            onResult(true, null)
        }.addOnFailureListener { authException ->
            onResult(false, authException)
        }
    }

    suspend fun saveIdPerangkat(user: IDPerangkatModel) {
        userPreference.saveIdPerangkat(user)
    }

    fun getIdPerangkat(): Flow<IDPerangkatModel> {
        return userPreference.getIdPerangkat()
    }

    suspend fun deleteIdPerangkat() {
        userPreference.logout()
    }

    fun logout() {
        auth.signOut()
        mqttClient.disconnect()
    }

    companion object {
        private const val TAG = "FireRepository"
        @Volatile
        private var instance: FireRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            context: Context
        ): FireRepository = instance ?: synchronized(this) {
            instance ?: FireRepository(userPreference, context).also { instance = it }
        }
    }
}


