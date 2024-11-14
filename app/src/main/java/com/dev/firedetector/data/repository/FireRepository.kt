package com.dev.firedetector.data.repository

import android.content.Context
import com.dev.firedetector.data.model.User
import com.dev.firedetector.util.Reference
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

    fun getFireData(onResult: (User?, Exception?) -> Unit){

    }

    fun logout() {
        auth.signOut()
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