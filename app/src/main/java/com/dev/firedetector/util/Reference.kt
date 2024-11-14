package com.dev.firedetector.util

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.dev.firedetector.R

object Reference {
    const val COLLECTION = "fire"
    const val COLLECTION2 = "api"

    fun isEmailValid(context: Context, email: String): Boolean {
        return if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, R.string.email_warning, Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    fun isPasswordValid(context: Context, password: String): Boolean {
        return if (TextUtils.isEmpty(password) || password.length < 6) {
            Toast.makeText(context, R.string.password_warning, Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }
}