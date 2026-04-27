package com.barradev.chester.core.model.repository

interface SessionManager {
    suspend fun getAccessToken(): String?
    suspend fun saveAccessToken(token: String)
    suspend fun clearSession()
}