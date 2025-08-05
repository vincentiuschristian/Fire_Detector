package com.dev.firedetector.data.retrofit

import com.dev.firedetector.data.pref.UserPreference
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {
        @Volatile
        private var INSTANCE: ApiService? = null

        fun getApiService(userPreference: UserPreference): ApiService {
            return INSTANCE ?: synchronized(this) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                val authInterceptor = Interceptor { chain ->
                    val token = runBlocking {
                        userPreference.getToken().firstOrNull() ?: ""
                    }

                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()

                    chain.proceed(request)
                }

                val client = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(authInterceptor)
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api-damkar.psti-ubl.id/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

                INSTANCE = retrofit.create(ApiService::class.java)
                INSTANCE!!
            }
        }
    }
}

