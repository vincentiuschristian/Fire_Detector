package com.dev.firedetector.data.retrofit

import com.dev.firedetector.core.data.source.pref.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreference: UserPreference
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking(Dispatchers.IO) {
            userPreference.getToken().first()
        }

        val newReq = chain.request().newBuilder().apply {
            if (token.isNotEmpty()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(newReq)
    }
}