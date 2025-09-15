package com.dev.firedetector.core.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.dev.firedetector.core.data.source.remote.network.ApiService
import com.dev.firedetector.core.data.source.remote.response.HistoryResponse
import com.dev.firedetector.core.data.source.remote.response.LoginRequest
import com.dev.firedetector.core.data.source.remote.response.LoginResponse
import com.dev.firedetector.core.data.source.remote.response.RegisterRequest
import com.dev.firedetector.core.data.source.remote.response.RegisterResponse
import com.dev.firedetector.core.data.source.remote.response.SensorDataResponse
import com.dev.firedetector.core.data.source.remote.response.UserResponse
import com.dev.firedetector.core.domain.model.UserModel
import com.dev.firedetector.core.data.source.mqtt.MqttClientHelper
import com.dev.firedetector.core.data.source.pref.UserPreference
import com.dev.firedetector.data.repository.IFireRepository
import com.dev.firedetector.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireRepositoryImpl @Inject constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val mqttClientHelper: MqttClientHelper
) : IFireRepository {

    override suspend fun loginUser(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.loginUser(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                val token = loginResponse.token
                val macList = loginResponse.macAddress ?: emptyList()

                if (token != null) {
                    userPreference.saveSession(UserModel(token, true), macList)
                    mqttClientHelper.subscribeTopics(macList)
                    Log.d("LoginUser", "Token & MAC list disimpan, MQTT subscribe dipanggil")
                    Result.Success(loginResponse)
                } else {
                    Result.Error("Token tidak valid dari server")
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
                        } catch (_: Exception) {
                            "Login gagal"
                        }
                        Result.Error("Error ${response.code()}: $errorBody")
                    }
                }
            }
        } catch (_: IOException) {
            Result.Error("Koneksi atau Server bermasalah!")
        } catch (e: Exception) {
            Log.e("LoginUser", "Exception: ${e.message}")
            Result.Error("Terjadi kesalahan: ${e.message ?: "Unknown error"}")
        }
    }

    override suspend fun initializeMqttSubscriptionsIfLoggedIn() {
        val user = userPreference.getSession().firstOrNull()
        if (user?.isLogin == true) {
            val macList = userPreference.getMacList().firstOrNull() ?: emptyList()
            mqttClientHelper.subscribeTopics(macList)
            Log.d("MQTT", "Subscribed automatically on startup with MAC list: $macList")
        }
    }

    override suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        location: String,
        role: String
    ): Result<RegisterResponse> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(username, email, password, location, role)
            val response = apiService.registerUser(request)

            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Registrasi berhasil tetapi tidak ada data yang diterima")
            } else {
                when (response.code()) {
                    400 -> {
                        val errorBody =
                            response.errorBody()?.string() ?: "Data registrasi tidak lengkap"
                        try {
                            val json = JSONObject(errorBody)
                            Result.Error(json.getString("error") ?: "Data registrasi tidak valid")
                        } catch (e: Exception) {
                            Result.Error(e.toString())
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
        } catch (_: IOException) {
            Result.Error("Tidak dapat terhubung ke server. Periksa koneksi internet Anda")
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan tak terduga: ${e.message ?: "Unknown error"}")
        }
    }

    override suspend fun getUser(): Result<UserResponse> {
        return try {
            val token = userPreference.getToken().firstOrNull() ?: ""
            Log.d("Get User Data", "Token User: $token")

            val response = apiService.getUserProfile()
            Log.d("API Response", "Code: ${response.code()}, Body: ${response.body()}")

            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) } ?: Result.Error("Data user kosong")
            } else {
                Result.Error("Gagal mendapatkan data user: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan: ${e.message}")
        }
    }

    override fun getMqttLiveData(): LiveData<Result<List<SensorDataResponse>>> {
        val resultLiveData = MutableLiveData<Result<List<SensorDataResponse>>>()
        val cachedList = mutableListOf<SensorDataResponse>()
        resultLiveData.value = Result.Loading

        mqttClientHelper.sensorLiveData.observeForever { item ->
            Log.d("MQTT_REPO", "Received: ${item?.macAddress} | CachedListSize: ${cachedList.size}")

            if (item != null) {
                val index = cachedList.indexOfFirst { it.macAddress == item.macAddress }
                if (index != -1) cachedList[index] = item else cachedList.add(item)
                Log.d("MQTT_REPO", "Updated CachedList: $cachedList")
                resultLiveData.postValue(Result.Success(cachedList.toList()))
            } else {
                resultLiveData.postValue(Result.Error("Data kosong dari MQTT"))
            }
        }

        return resultLiveData
    }

    override fun getSensorHistory(macAddress: String): LiveData<Result<HistoryResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getHistory(macAddress)
                emit(Result.Success(response))
            } catch (_: HttpException) {
                emit(Result.Error("Failed to load history"))
            } catch (_: Exception) {
                emit(Result.Error("Internet Connection Problem"))
            }
        }

    override fun getFilteredHistory(macAddress: String, range: String): LiveData<Result<HistoryResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getFilteredHistory(macAddress, range)
                emit(Result.Success(response))
            } catch (_: HttpException) {
                emit(Result.Error("Failed to load filtered history"))
            } catch (_: Exception) {
                emit(Result.Error("Internet Connection Problem"))
            }
        }

    override suspend fun logout(): Result<String> {
        return try {
            val response = apiService.logout()
            if (response.isSuccessful) {
                userPreference.logout()
                Log.d("Logout Berhasil", "Token berhasil dihapus: ${userPreference.getToken()}")
                Result.Success(response.body()?.message ?: "Logout berhasil")
            } else {
                val errorBody = response.errorBody()?.string() ?: "Logout gagal!"
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan: ${e.message}")
        }
    }

    override fun getSession() = userPreference.getSession()

    override suspend fun deleteIdPerangkat() = userPreference.logout()
}