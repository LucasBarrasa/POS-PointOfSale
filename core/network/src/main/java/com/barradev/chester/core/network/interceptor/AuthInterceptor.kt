package com.barradev.chester.core.network.interceptor

import com.barradev.chester.core.model.repository.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking {
            sessionManager.getAccessToken()
        }

        val requestBuilder = originalRequest.newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "token $token")
        }

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }
}