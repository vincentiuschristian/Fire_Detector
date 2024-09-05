package com.dev.firedetector.data.repository

import com.dev.firedetector.data.model.User
import com.dev.firedetector.util.Reference
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FireRepository() {
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
}