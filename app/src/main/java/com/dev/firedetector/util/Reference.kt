package com.dev.firedetector.util

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dev.firedetector.R
import com.google.android.material.card.MaterialCardView

object Reference {

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

fun MaterialCardView.setDangerState(isDanger: Boolean, context: Context) {
    val colorRes = if (isDanger) R.color.red else R.color.cardview_color
    setCardBackgroundColor(ContextCompat.getColor(context, colorRes))
}

