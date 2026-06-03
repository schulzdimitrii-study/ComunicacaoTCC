package com.pedroaba.tccmobile.auth

import android.util.Log
import com.pedroaba.tccmobile.auth.http.AuthHttpClient
import com.pedroaba.tccmobile.auth.model.AuthResponse
import com.pedroaba.tccmobile.auth.model.LoginRequest
import com.pedroaba.tccmobile.auth.model.RegisterRequest
import com.pedroaba.tccmobile.auth.model.UserSession
import com.pedroaba.tccmobile.auth.session.SessionManager
import kotlinx.coroutines.flow.Flow

private const val TAG = "AuthRepository"

sealed class AuthResult {
    data class Success(val session: UserSession) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val httpClient: AuthHttpClient,
    private val sessionManager: SessionManager
) {
    val userSession: Flow<UserSession> = sessionManager.userSession

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = httpClient.login(LoginRequest(email, password))
            result.fold(
                onSuccess = { response ->
                    val session = response.toUserSession()
                    sessionManager.saveSession(session)
                    AuthResult.Success(session)
                },
                onFailure = { error ->
                    Log.e(TAG, "Login failed", error)
                    AuthResult.Error(error.message ?: "Erro ao fazer login")
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Login exception", e)
            AuthResult.Error(e.message ?: "Erro ao fazer login")
        }
    }

    suspend fun register(
        email: String,
        name: String,
        password: String,
        birthDate: String? = null,
        height: Double? = null,
        weight: Double? = null
    ): AuthResult {
        return try {
            val birthdayDate = parseBirthDate(birthDate)
            val result = httpClient.register(
                RegisterRequest(
                    email = email,
                    name = name,
                    password = password,
                    birthdayDate = birthdayDate,
                    height = height,
                    weight = weight
                )
            )
            result.fold(
                onSuccess = { response ->
                    val session = response.toUserSession()
                    sessionManager.saveSession(session)
                    AuthResult.Success(session)
                },
                onFailure = { error ->
                    Log.e(TAG, "Register failed", error)
                    AuthResult.Error(error.message ?: "Erro ao criar conta")
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Register exception", e)
            AuthResult.Error(e.message ?: "Erro ao criar conta")
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    private fun parseBirthDate(birthDate: String?): String? {
        if (birthDate.isNullOrBlank()) return null
        val digits = birthDate.filter { it.isDigit() }
        if (digits.length != 8) return null
        return "${digits.slice(4..7)}-${digits.slice(2..3)}-${digits.slice(0..1)}"
    }

    private fun AuthResponse.toUserSession() = UserSession(
        token = token,
        userId = userId,
        name = name,
        email = email
    )
}