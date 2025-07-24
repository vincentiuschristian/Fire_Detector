package com.dev.firedetector.data.api

import com.dev.firedetector.data.response.HistoryResponse
import com.dev.firedetector.data.response.LoginRequest
import com.dev.firedetector.data.response.LoginResponse
import com.dev.firedetector.data.response.LogoutResponse
import com.dev.firedetector.data.response.RegisterRequest
import com.dev.firedetector.data.response.RegisterResponse
import com.dev.firedetector.data.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("/users/register")
    suspend fun registerUser(
        @Body user: RegisterRequest
    ): Response<RegisterResponse>

    @POST("/users/login")
    suspend fun loginUser(
        @Body login: LoginRequest
    ): Response<LoginResponse>

    @GET("/user/profile")
    suspend fun getUserProfile(
    ): Response<UserResponse>

    @POST("users/logout")
    suspend fun logout(): Response<LogoutResponse>

    @GET("/sensor/history/{mac}")
    suspend fun getHistory(
        @Path("mac") macAddress: String
    ): HistoryResponse

    @GET("/sensor/history/{mac}")
    suspend fun getFilteredHistory(
        @Path("mac") macAddress: String,
        @retrofit2.http.Query("range") range: String
    ): HistoryResponse
}