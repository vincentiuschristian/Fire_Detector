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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

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
            println("Error fetching user data: ${e.message}") // Debug log
            null
        }
    }


    suspend fun getSensorData(): List<DataAlatModel> {
        return try {
            val id = userPreference.getIdPerangkat().first().idPerangkat
            val querySnapshot = db.collection(Reference.COLLECTION)
                .document(id)
                .collection(Reference.DATAALAT)
                .get()
                .await()

            querySnapshot.documents.map { documentSnapshot ->
                DataAlatModel(
                    flameDetected = documentSnapshot.getString(Reference.FIELD_FLAME_DETECTED),
                    hum = documentSnapshot.getDouble(Reference.FIELD_HUM),
                    mqValue = documentSnapshot.getString(Reference.FIELD_GAS_LEVEL),
                    temp = documentSnapshot.getDouble(Reference.FIELD_TEMP),
                    timestamp = documentSnapshot.getString(Reference.FIELD_TIMESTAMP),
                    deviceId = id
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
