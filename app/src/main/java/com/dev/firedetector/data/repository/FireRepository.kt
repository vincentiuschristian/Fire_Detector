package com.dev.firedetector.data.repository

import android.content.Context
import com.dev.firedetector.data.api.ApiService
import com.dev.firedetector.data.model.LoginRequest
import com.dev.firedetector.data.model.LoginResponse
import com.dev.firedetector.data.model.RegisterRequest
import com.dev.firedetector.data.model.RegisterResponse
import com.dev.firedetector.data.model.SensorDataResponse
import com.dev.firedetector.data.pref.IDPerangkatModel
import com.dev.firedetector.data.pref.UserPreference
import com.dev.firedetector.util.Result
import kotlinx.coroutines.flow.Flow

class FireRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    context: Context
) {

    // Register
    suspend fun registerUser(
        deviceId: String,
        username: String,
        email: String,
        password: String,
        location: String
    ): Result<RegisterResponse> {
        return try {
            val response = apiService.registerUser(
                RegisterRequest(
                    deviceId,
                    username,
                    email,
                    password,
                    location
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.body()?.error ?: "Registrasi gagal!")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Terjadi kesalahan pada jaringan!")
        }
    }

    // Login
    suspend fun loginUser(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.loginUser(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.body()?.error ?: "Login gagal!")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Terjadi kesalahan pada jaringan!")
        }
    }

    // Fungsi untuk mendapatkan data sensor terbaru
    suspend fun getLatestSensorData(): Result<SensorDataResponse> {
        return try {
            val response = apiService.getLatestSensorData()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.body()?.toString() ?: "Gagal mendapatkan data sensor terbaru")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Terjadi kesalahan pada jaringan")
        }
    }

    // Fungsi untuk mendapatkan semua data history sensor
    suspend fun getSensorHistory(): Result<List<SensorDataResponse>> {
        return try {
            val response = apiService.getSensorHistory()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.body()?.toString() ?: "Gagal mendapatkan data history sensor")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Terjadi kesalahan pada jaringan")
        }
    }


    suspend fun saveIdPerangkat(user: IDPerangkatModel) {
        userPreference.saveIdPerangkat(user)
    }

    fun getIdPerangkat(): Flow<IDPerangkatModel> {
        return userPreference.getIdPerangkat()
    }

    suspend fun deleteIdPerangkat() {
        userPreference.logout()
    }


    companion object {

        @Volatile
        private var instance: FireRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            context: Context
        ): FireRepository = instance ?: synchronized(this) {
            instance ?: FireRepository(userPreference, apiService, context).also { instance = it }
        }
    }
}


