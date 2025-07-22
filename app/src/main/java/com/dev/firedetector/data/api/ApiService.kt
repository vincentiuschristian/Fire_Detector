package com.dev.firedetector.data.api

import com.dev.firedetector.data.response.LoginRequest
import com.dev.firedetector.data.response.LoginResponse
import com.dev.firedetector.data.response.LogoutResponse
import com.dev.firedetector.data.response.RegisterRequest
import com.dev.firedetector.data.response.RegisterResponse
import com.dev.firedetector.data.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/users/register")
    suspend fun registerUser(
        @Body user: RegisterRequest
    ): Response<RegisterResponse>

    @POST("/users/login")
    suspend fun loginUser(
        @Body login: LoginRequest
    ): Response<LoginResponse>

    @GET("/api/user/profile")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): Response<UserResponse>


    @POST("/api/logout")
    suspend fun logout(): Response<LogoutResponse>
}