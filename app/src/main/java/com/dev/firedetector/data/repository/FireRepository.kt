package com.dev.firedetector.data.repository

import android.util.Log
import androidx.datastore.core.IOException
import com.dev.firedetector.data.api.ApiService
import com.dev.firedetector.data.model.UserModel
import com.dev.firedetector.data.pref.UserPreference
import com.dev.firedetector.data.response.DeviceLocationResponse
import com.dev.firedetector.data.response.DeviceLocationUpdate
import com.dev.firedetector.data.response.LoginRequest
import com.dev.firedetector.data.response.LoginResponse
import com.dev.firedetector.data.response.RegisterRequest
import com.dev.firedetector.data.response.RegisterResponse
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.data.response.UserResponse
import com.dev.firedetector.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class FireRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun loginUser(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.loginUser(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                val token = loginResponse.token

                if (!token.isNullOrEmpty()) {
                    userPreference.saveSession(UserModel(token, true))
                    Log.d("LoginUser", "Token berhasil disimpan di DataStore: $token")
                } else {
                    Log.e("LoginUser", "Token kosong dalam response!")
                }

                Result.Success(loginResponse)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Login gagal!"
                Log.e("LoginUser", "Error ${response.code()}: $errorBody")
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: Exception) {
            Log.e("LoginUser", "Exception: ${e.message}")
            Result.Error("Terjadi kesalahan: ${e.message}")
        }
    }

    suspend fun registerUser(
        deviceId: String,
        username: String,
        email: String,
        password: String,
        location: String
    ): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(deviceId, username, email, password, location)
                val response = apiService.registerUser(request)

                if (response.isSuccessful) {
                    Log.d("UserRepository", "Registration successful")
                    response.body()?.let {
                        Log.d("UserRepository", "Response body: $it")
                        Result.Success(it)
                    } ?: run {
                        Log.e("UserRepository", "Registration successful but response body is empty")
                        Result.Error("Registration successful but response body is empty")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Registration failed!"
                    Log.e("UserRepository", "Registration failed with code: ${response.code()}, error: $errorBody")
                    Result.Error("Error ${response.code()}: $errorBody")
                }
            } catch (e: HttpException) {
                Log.e("UserRepository", "HTTP Error: ${e.message()}")
                Result.Error("HTTP Error: ${e.message()}")
            } catch (e: IOException) {
                Log.e("UserRepository", "Network Error: ${e.message}")
                Result.Error("Network Error: ${e.message}")
            } catch (e: Exception) {
                Log.e("UserRepository", "An unexpected error occurred: ${e.message}")
                Result.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    suspend fun getUser(): Result<UserResponse> {
        return try {
            val response = apiService.getUser()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Gagal mendapatkan data user"
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan pada jaringan: ${e.message}")
        }
    }

    suspend fun getLatestDataZona1(): Result<SensorDataResponse> {
        return try {
            val response = apiService.getLatestDataZona1()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Gagal mendapatkan data sensor terbaru"
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan pada jaringan: ${e.message}")
        }
    }

    suspend fun getSensorHistoryZona1(): Result<List<SensorDataResponse>> {
        return try {
            val response = apiService.getSensorHistoryZona1()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Gagal mendapatkan data history sensor"
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan pada jaringan: ${e.message}")
        }
    }

    suspend fun getLatestDataZona2(): Result<SensorDataResponse> {
        return try {
            val response = apiService.getLatestDataZona2()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Gagal mendapatkan data sensor terbaru"
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan pada jaringan: ${e.message}")
        }
    }

    suspend fun getSensorHistoryZona2(): Result<List<SensorDataResponse>> {
        return try {
            val response = apiService.getSensorHistoryZona2()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Gagal mendapatkan data history sensor"
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan pada jaringan: ${e.message}")
        }
    }

    suspend fun getDeviceLocations(): Result<List<DeviceLocationResponse>> {
        return try {
            val response = apiService.getDeviceLocations()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Failed to get device locations"
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: HttpException) {
            Result.Error("HTTP Error: ${e.message()}")
        } catch (e: IOException) {
            Result.Error("Network Error: ${e.message}")
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun updateDeviceLocations(locations: List<DeviceLocationUpdate>): Result<String> {
        return try {
            val response = apiService.updateDeviceLocations(locations)
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Locations updated successfully"
                Result.Success(message)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Failed to update locations"
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: HttpException) {
            Result.Error("HTTP Error: ${e.message()}")
        } catch (e: IOException) {
            Result.Error("Network Error: ${e.message}")
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun logout(): Result<String> {
        return try {
            val response = apiService.logout()
            if (response.isSuccessful) {
                userPreference.logout()
                Result.Success(response.body()?.message ?: "Logout berhasil")
            } else {
                val errorBody = response.errorBody()?.string() ?: "Logout gagal!"
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan: ${e.message}")
        }
    }


    fun getSession(): Flow<UserModel> = userPreference.getSession()

    suspend fun deleteIdPerangkat() = userPreference.logout()

    companion object {

        @Volatile
        private var instance: FireRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): FireRepository = instance ?: synchronized(this) {
            instance ?: FireRepository(userPreference, apiService).also { instance = it }
        }
    }
}


