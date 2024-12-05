package com.dev.firedetector.data.repository

import com.dev.firedetector.data.model.DataAlatModel
import com.dev.firedetector.data.model.DataUserModel
import com.dev.firedetector.data.pref.UserModel
import com.dev.firedetector.data.pref.UserPreference
import com.dev.firedetector.util.Reference
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FireRepository(private val userPreference: UserPreference) {
    private val auth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    fun getUser(): String? = auth.uid

    suspend fun saveIdPerangkat(user: UserModel) {
        userPreference.saveIdPerangkat(user)
    }

    suspend fun getIdPerangkat(): Flow<UserModel> {
        return userPreference.getIdPerangkat()
    }

    suspend fun deleteIdPerangkat() {
        userPreference.logout()
    }

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


    fun getUserData(onResult: (String?, Exception?) -> Unit) {
        db.collection(Reference.COLLECTION)
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val idPerangkat = documentSnapshot.getString("idPerangkat")
                if (idPerangkat != null) {
                    onResult(idPerangkat, null)
                } else {
                    onResult(null, Exception("ID perangkat tidak ditemukan"))
                }
            }
            .addOnFailureListener { e ->
                onResult(null, e)
            }
    }


//    suspend fun getSensorData(): List<DataFire> {
//        return try {
//            val querySnapshot = db.collection(Reference.COLLECTION_API).get().await()
//            querySnapshot.documents.map { documentSnapshot ->
//                val temp = documentSnapshot.getDouble(Reference.FIELD_TEMP) ?: 0.0
//                val hum = documentSnapshot.getDouble(Reference.FIELD_HUM) ?: 0.0
//                val gasLevel = documentSnapshot.getDouble(Reference.FIELD_GAS_LEVEL) ?: 0.0
//                val flameDetected = documentSnapshot.getBoolean(Reference.FIELD_FLAME_DETECTED)
//
//                DataFire(temp = temp, hum = hum, mqValue = gasLevel, flameDetected = flameDetected)
//            }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }

    suspend fun getUserDeviceId(): String {
        return suspendCoroutine { continuation ->
            db.collection(Reference.COLLECTION).document(auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val dataUserModel = documentSnapshot.toObject(DataUserModel::class.java)
                    if (dataUserModel != null && !dataUserModel.idPerangkat.isNullOrEmpty()) {
                        continuation.resume(dataUserModel.idPerangkat!!)
                    } else {
                        continuation.resumeWithException(Exception("ID perangkat tidak ditemukan"))
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    // Fetch sensor data using device ID
    suspend fun getSensorData(idPerangkat: String): List<DataAlatModel> {
        return try {
            val querySnapshot = db.collection(Reference.COLLECTION)
                .document(idPerangkat)
                .collection(Reference.DATAALAT)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.map { documentSnapshot ->
                DataAlatModel(
                    flameDetected = documentSnapshot.getString("FlameDetected"),
                    hum = documentSnapshot.getDouble("Humidity"),
                    mqValue = documentSnapshot.getString("MQValue"),
                    temp = documentSnapshot.getDouble("Temperature"),
                    timestamp = documentSnapshot.getString("timestamp"),
                    deviceId = idPerangkat
                )
            }
        } catch (e: Exception) {
            println("Error fetching sensor data: ${e.message}") // Debug log
            emptyList()
        }
    }

    fun logout() {
        auth.signOut()
    }

    companion object {
        @Volatile
        private var instances: FireRepository? = null

        fun getInstance(userPreference: UserPreference): FireRepository =
            instances ?: synchronized(this) {
                instances ?: FireRepository(userPreference)
                    .also {
                        instances = it
                    }
            }
    }
}
