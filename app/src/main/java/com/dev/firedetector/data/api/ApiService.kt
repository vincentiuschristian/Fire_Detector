package com.dev.firedetector.data.api

import com.dev.firedetector.data.response.LoginRequest
import com.dev.firedetector.data.response.LoginResponse
import com.dev.firedetector.data.response.RegisterRequest
import com.dev.firedetector.data.response.RegisterResponse
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.data.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/api/register")
    suspend fun registerUser(
        @Body user: RegisterRequest
    ): Response<RegisterResponse>

    @POST("/api/login")
    suspend fun loginUser(
        @Body login: LoginRequest
    ): Response<LoginResponse>

    @GET("/api/ruangtamu/latest")
    suspend fun getLatestDataRuangTamu(): Response<SensorDataResponse>

    @GET("/api/ruangtamu/history")
    suspend fun getSensorHistoryRuangTamu(): Response<List<SensorDataResponse>>

    @GET("/api/kamar/latest")
    suspend fun getLatestDataKamar(): Response<SensorDataResponse>

    @GET("/api/kamar/history")
    suspend fun getSensorHistoryKamar(): Response<List<SensorDataResponse>>

    @GET("/api/user/profile")
    suspend fun getUser(): Response<UserResponse>
}