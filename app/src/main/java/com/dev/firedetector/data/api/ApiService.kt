package com.dev.firedetector.data.api

import com.dev.firedetector.data.model.LoginRequest
import com.dev.firedetector.data.model.LoginResponse
import com.dev.firedetector.data.model.RegisterRequest
import com.dev.firedetector.data.model.RegisterResponse
import com.dev.firedetector.data.model.SensorDataResponse
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

    @GET("/api/sensor/latest")
    suspend fun getLatestSensorData(): Response<SensorDataResponse>

    @GET("/api/sensor/history")
    suspend fun getSensorHistory(): Response<List<SensorDataResponse>>
}