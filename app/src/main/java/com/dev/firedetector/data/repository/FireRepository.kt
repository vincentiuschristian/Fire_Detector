package com.dev.firedetector.data.repository

import android.content.Context
import com.dev.firedetector.data.model.DataFire
import com.dev.firedetector.data.model.User
import com.dev.firedetector.util.Reference
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FireRepository(context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    fun getUser():String? = auth.uid

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
        user: User,
        email: String,
        password: String,
        onResult: (Void?, Exception?) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            db.collection(Reference.COLLECTION).document(it.user!!.uid)
                .set(user)
                .addOnSuccessListener { documentReference ->
                    onResult(documentReference, null)
                }
                .addOnFailureListener { e ->
                    onResult(null, e)
                }
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getUserData(onResult: (User?, Exception?) -> Unit) {
        db.collection(Reference.COLLECTION).document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                onResult(user, null)
            }
            .addOnFailureListener { e ->
                onResult(null, e)
            }
    }

    suspend fun getSensorData(): List<DataFire> {
        return try {
            val querySnapshot = db.collection(Reference.COLLECTION_API).get().await()
            querySnapshot.documents.map { documentSnapshot ->
                val temp = documentSnapshot.getDouble(Reference.FIELD_TEMP) ?: 0.0
                val hum = documentSnapshot.getDouble(Reference.FIELD_HUM) ?: 0.0
                val gasLevel = documentSnapshot.getDouble(Reference.FIELD_GAS_LEVEL) ?: 0.0
                val flameDetected = documentSnapshot.getBoolean(Reference.FIELD_FLAME_DETECTED)

                DataFire(temp, hum, gasLevel, flameDetected)
            }
        } catch (e: Exception) {
            emptyList() // Kembalikan list kosong jika ada error
        }
    }


    companion object {
        @Volatile
        private var instances: FireRepository? = null

        fun getInstance(context: Context):FireRepository =
            instances ?: synchronized(this){
                instances ?:FireRepository(context)
                    .also {
                        instances = it
                    }
            }
    }
}