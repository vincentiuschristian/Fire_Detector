package com.dev.firedetector.di

import android.content.Context
import com.dev.firedetector.data.pref.UserPreference
import com.dev.firedetector.data.pref.dataStore
import com.dev.firedetector.data.repository.FireRepository

object Injection {
    fun provideRepository(context: Context): FireRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return FireRepository.getInstance(pref, context)
    }
}
