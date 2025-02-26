package com.dev.firedetector.data.repository

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.dev.firedetector.data.api.ApiService
import com.dev.firedetector.data.model.DataAlatModel
import com.dev.firedetector.data.model.DataUserModel
import com.dev.firedetector.data.pref.IDPerangkatModel
import com.dev.firedetector.data.pref.UserPreference
import com.dev.firedetector.util.MqttConnectionState
import com.dev.firedetector.util.Reference
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject

class FireRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    context: Context
) {
    private val appContext = context.applicationContext
    private val auth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    // MQTT Configuration
    private val mqttClient: MqttAndroidClient
    private val _sensorData = MutableStateFlow<List<DataAlatModel>>(emptyList())
    val sensorData: StateFlow<List<DataAlatModel>> = _sensorData

    // MQTT State Management
    private val _mqttState = MutableStateFlow<MqttConnectionState>(MqttConnectionState.Disconnected)

    init {
        val serverURI = "ssl://d7d8ee83.ala.asia-southeast1.emqxsl.com:8883"

        val clientId = "android_client_${Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)}"

        mqttClient = MqttAndroidClient(appContext, serverURI, clientId)
        setupMqttConnection()
    }

    private fun setupMqttConnection() {
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String, message: MqttMessage) {
                handleIncomingMessage(String(message.payload))
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
        if (mqttClient.isConnected) {
            Log.d(TAG, "MQTT already connected")
            return
        }

        val options = MqttConnectOptions().apply {
            isCleanSession = false  // Agar tetap terhubung meskipun aplikasi ditutup
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
                    Log.d(TAG, "MQTT Connected Successfully")
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

    private fun handleIncomingMessage(payload: String) {
        try {
            val jsonObject = JSONObject(payload)
            val data = DataAlatModel(
                humidity = jsonObject.getDouble("humidity").toFloat(),
                temperature = jsonObject.getDouble("temperature").toFloat(),
                flameStatus = jsonObject.getString("flameStatus"),
                mqStatus = jsonObject.getString("mqStatus")
            )

            // Update sensorData dengan data baru
            _sensorData.update { currentList ->
                val newList = currentList.toMutableList() // Konversi ke MutableList
                newList.add(0, data) // Tambahkan data baru di awal list
                if (newList.size > 100) {
                    newList.removeAt(newList.size - 1) // Hapus data terlama jika melebihi 100 item
                }
                newList // Kembalikan sebagai List
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing MQTT message: ${e.message}")
        }
    }
    fun subscribeToTopic(topic: String) {
        if (!mqttClient.isConnected) {
            Log.e(TAG, "Cannot subscribe, MQTT is not connected")
            return
        }

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
        if (!mqttClient.isConnected) {
            Log.d(TAG, "MQTT is already disconnected")
            return
        }

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
        deviceId: String,
        onResult: (Boolean, Exception?) -> Unit,
    ) {
        db.collection(Reference.COLLECTION).document(deviceId).get().addOnSuccessListener {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                val userId = it.user!!.uid

                db.collection(Reference.COLLECTION).document(deviceId)
                    .collection(Reference.DATAUSER).document(userId)
                    .set(dataUserModel.apply { this.email = email })
                    .addOnSuccessListener {
                        onResult(true, null)
                    }
                    .addOnFailureListener { e ->
                        onResult(false, e)
                    }
            }.addOnFailureListener { authException ->
                onResult(false, authException)
            }
        }.addOnFailureListener { exception ->
            onResult(false, exception)
        }
    }

    suspend fun getUserData(): DataUserModel? {
        return try {
            val id = userPreference.getIdPerangkat().first().idPerangkat
            val querySnapshot = db.collection(Reference.COLLECTION)
                .document(id)
                .collection(Reference.DATAUSER)
                .get()
                .await()

            val documentSnapshot = querySnapshot.documents.firstOrNull()
            documentSnapshot?.toObject(DataUserModel::class.java)
        } catch (e: Exception) {
            println("Error fetching user data: ${e.message}")
            null
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
            apiService: ApiService,
            context: Context
        ): FireRepository = instance ?: synchronized(this) {
            instance ?: FireRepository(userPreference, apiService, context).also { instance = it }
        }
    }
}


