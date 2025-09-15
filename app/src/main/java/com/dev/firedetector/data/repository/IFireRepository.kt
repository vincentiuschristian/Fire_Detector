package com.dev.firedetector.data.repository

import androidx.lifecycle.LiveData
import com.dev.firedetector.core.data.source.remote.response.HistoryResponse
import com.dev.firedetector.core.data.source.remote.response.LoginResponse
import com.dev.firedetector.core.data.source.remote.response.RegisterResponse
import com.dev.firedetector.core.data.source.remote.response.SensorDataResponse
import com.dev.firedetector.core.data.source.remote.response.UserResponse
import com.dev.firedetector.core.domain.model.UserModel
import com.dev.firedetector.util.Result
import kotlinx.coroutines.flow.Flow

interface IFireRepository {
    suspend fun loginUser(email: String, password: String): Result<LoginResponse>
    suspend fun initializeMqttSubscriptionsIfLoggedIn()
    suspend fun registerUser(username: String, email: String, password: String, location: String, role: String = "user"): Result<RegisterResponse>
    suspend fun getUser(): Result<UserResponse>

    fun getMqttLiveData(): LiveData<Result<List<SensorDataResponse>>>
    fun getSensorHistory(macAddress: String): LiveData<Result<HistoryResponse>>
    fun getFilteredHistory(macAddress: String, range: String): LiveData<Result<HistoryResponse>>

    suspend fun logout(): Result<String>
    fun getSession(): Flow<UserModel>
    suspend fun deleteIdPerangkat()
}