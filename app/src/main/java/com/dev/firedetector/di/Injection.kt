package com.dev.firedetector.di

import android.content.Context
import com.dev.firedetector.data.repository.FireRepository

object Injection {
    fun provideRepository(context: Context): FireRepository {
        return FireRepository.getInstance(context)
    }
}