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
import org.json.JSONObject
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
                    Result.Success(loginResponse)
                } else {
                    Log.e("LoginUser", "Token kosong dalam response!")
                    Result.Error("Gagal login: Token tidak valid dari server")
                }
            } else {
                when (response.code()) {
                    400 -> Result.Error("Email dan password harus diisi")
                    401 -> Result.Error("Email atau password salah")
                    500 -> Result.Error("Server error: Gagal memproses login")
                    else -> {
                        val errorBody = try {
                            response.errorBody()?.string()?.let {
                                val json = JSONObject(it)
                                json.getString("error") ?: "Login gagal"
                            } ?: "Login gagal"
                        } catch (e: Exception) {
                            "Login gagal"
                        }
                        Result.Error("Error ${response.code()}: $errorBody")
                    }
                }
            }
        } catch (e: IOException) {
            Result.Error("Koneksi atau Server bermasalah!")
        } catch (e: Exception) {
            Log.e("LoginUser", "Exception: ${e.message}")
            Result.Error("Terjadi kesalahan: ${e.message ?: "Unknown error"}")
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
                    response.body()?.let {
                        Result.Success(it)
                    } ?: Result.Error("Registrasi berhasil tetapi tidak ada data yang diterima")
                } else {
                    when (response.code()) {
                        400 -> {
                            val errorBody = response.errorBody()?.string() ?: "Data registrasi tidak lengkap"
                            try {
                                val json = JSONObject(errorBody)
                                Result.Error(json.getString("error") ?: "Data registrasi tidak valid")
                            } catch (e: Exception) {
                                Result.Error(errorBody)
                            }
                        }
                        409 -> Result.Error("Email atau username sudah terdaftar")
                        500 -> Result.Error("Server error: Gagal memproses registrasi")
                        else -> {
                            val errorBody = response.errorBody()?.string() ?: "Registrasi gagal"
                            Result.Error("Error ${response.code()}: $errorBody")
                        }
                    }
                }
            } catch (e: HttpException) {
                Result.Error("Error server: ${e.message()}")
            } catch (e: IOException) {
                Result.Error("Tidak dapat terhubung ke server. Periksa koneksi internet Anda")
            } catch (e: Exception) {
                Result.Error("Terjadi kesalahan tak terduga: ${e.message ?: "Unknown error"}")
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
                when (response.code()) {
                    401 -> Result.Error("Sesi telah berakhir, silakan login kembali")
                    404 -> Result.Error("Data sensor terbaru tidak ditemukan")
                    503 -> Result.Error("Server sibuk, coba lagi nanti")
                    else -> {
                        val errorBody = try {
                            response.errorBody()?.string()?.let {
                                JSONObject(it).getString("message") ?: "Gagal memuat data sensor"
                            } ?: "Gagal memuat data sensor"
                        } catch (e: Exception) {
                            "Gagal memuat data sensor"
                        }
                        Result.Error("Error ${response.code()}: $errorBody")
                    }
                }
            }
        } catch (e: IOException) {
            Result.Error("Tidak dapat terhubung ke server. Periksa koneksi internet Anda")
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan: ${e.message ?: "Gagal memuat data sensor"}")
        }
    }

    suspend fun getSensorHistoryZona1(): Result<List<SensorDataResponse>> {
        return try {
            val response = apiService.getSensorHistoryZona1()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                when (response.code()) {
                    401 -> Result.Error("Sesi telah berakhir, silakan login kembali")
                    404 -> Result.Error("Riwayat sensor tidak ditemukan")
                    else -> {
                        val errorBody = try {
                            response.errorBody()?.string()?.let {
                                JSONObject(it).getString("message") ?: "Gagal memuat riwayat sensor"
                            } ?: "Gagal memuat riwayat sensor"
                        } catch (e: Exception) {
                            "Gagal memuat riwayat sensor"
                        }
                        Result.Error("Error ${response.code()}: $errorBody")
                    }
                }
            }
        } catch (e: IOException) {
            Result.Error("Tidak dapat terhubung ke server. Periksa koneksi internet Anda")
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan: ${e.message ?: "Gagal memuat riwayat sensor"}")
        }
    }

    suspend fun getLatestDataZona2(): Result<SensorDataResponse> {
        return try {
            val response = apiService.getLatestDataZona2()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                when (response.code()) {
                    401 -> Result.Error("Sesi telah berakhir, silakan login kembali")
                    404 -> Result.Error("Data sensor terbaru tidak ditemukan")
                    503 -> Result.Error("Server sibuk, coba lagi nanti")
                    else -> {
                        val errorBody = try {
                            response.errorBody()?.string()?.let {
                                JSONObject(it).getString("message") ?: "Gagal memuat data sensor"
                            } ?: "Gagal memuat data sensor"
                        } catch (e: Exception) {
                            "Gagal memuat data sensor"
                        }
                        Result.Error("Error ${response.code()}: $errorBody")
                    }
                }
            }
        } catch (e: IOException) {
            Result.Error("Tidak dapat terhubung ke server. Periksa koneksi internet Anda")
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan: ${e.message ?: "Gagal memuat data sensor"}")
        }
    }

    suspend fun getSensorHistoryZona2(): Result<List<SensorDataResponse>> {
        return try {
            val response = apiService.getSensorHistoryZona2()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                when (response.code()) {
                    401 -> Result.Error("Sesi telah berakhir, silakan login kembali")
                    404 -> Result.Error("Riwayat sensor tidak ditemukan")
                    else -> {
                        val errorBody = try {
                            response.errorBody()?.string()?.let {
                                JSONObject(it).getString("message") ?: "Gagal memuat riwayat sensor"
                            } ?: "Gagal memuat riwayat sensor"
                        } catch (e: Exception) {
                            "Gagal memuat riwayat sensor"
                        }
                        Result.Error("Error ${response.code()}: $errorBody")
                    }
                }
            }
        } catch (e: IOException) {
            Result.Error("Tidak dapat terhubung ke server. Periksa koneksi internet Anda")
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan: ${e.message ?: "Gagal memuat riwayat sensor"}")
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


