package com.dev.firedetector.di

import android.content.Context
import com.dev.firedetector.data.api.ApiConfig
import com.dev.firedetector.data.mqtt.MqttClientHelper
import com.dev.firedetector.data.pref.UserPreference
import com.dev.firedetector.data.pref.dataStore
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.util.NotificationHelper

object Injection {
    fun provideRepository(context: Context): FireRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        val notificationHelper = NotificationHelper(context)
        val mqttClientHelper = MqttClientHelper()
        return FireRepository.getInstance(pref, apiService, mqttClientHelper)
    }
}
