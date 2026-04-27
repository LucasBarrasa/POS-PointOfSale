package com.barradev.chester.core.data.repository

import com.barradev.chester.core.model.repository.SessionManager
import javax.inject.Inject

class FakeSessionManagerImpl @Inject constructor() : SessionManager {


    override suspend fun getAccessToken(): String {
        return "Access Tokens almacenados en EncryptedSharedPreferences "
    }

    override suspend fun saveAccessToken(token: String) {
        // Se almacenan en EncryptedSharedPreferences
    }

    override suspend fun clearSession() {
        // Dar de baja la sesion y limpiar token
    }

}