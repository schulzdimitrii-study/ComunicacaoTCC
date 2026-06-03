package com.pedroaba.tccmobile.auth

import android.content.Context
import android.util.Log
import com.pedroaba.tccmobile.auth.http.AuthHttpClient
import com.pedroaba.tccmobile.auth.model.UserSession
import com.pedroaba.tccmobile.auth.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "AuthManager"

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val session: UserSession) : AuthState()
}

class AuthManager(context: Context) {

    private val httpClient = AuthHttpClient()
    private val sessionManager = SessionManager(context)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        scope.launch {
            sessionManager.userSession.collect { session ->
                _authState.value = if (session.isValid()) {
                    Log.d(TAG, "Session restored: ${session.email}")
                    AuthState.Authenticated(session)
                } else {
                    AuthState.Unauthenticated
                }
            }
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            val result = httpClient.login(com.pedroaba.tccmobile.auth.model.LoginRequest(email, password))
            result.fold(
                onSuccess = { response ->
                    val session = UserSession(response.token, response.userId, response.name, response.email)
                    sessionManager.saveSession(session)
                    _authState.value = AuthState.Authenticated(session)
                    Log.d(TAG, "Login success: ${session.email}")
                    AuthResult.Success(session)
                },
                onFailure = { error ->
                    Log.e(TAG, "Login failed", error)
                    AuthResult.Error(error.message ?: "Erro ao fazer login")
                }
            )
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
        return withContext(Dispatchers.IO) {
            val request = com.pedroaba.tccmobile.auth.model.RegisterRequest(
                email, name, password, parseBirthDate(birthDate), height, weight
            )
            val result = httpClient.register(request)
            result.fold(
                onSuccess = { response ->
                    val session = UserSession(response.token, response.userId, response.name, response.email)
                    sessionManager.saveSession(session)
                    _authState.value = AuthState.Authenticated(session)
                    Log.d(TAG, "Register success: ${session.email}")
                    AuthResult.Success(session)
                },
                onFailure = { error ->
                    Log.e(TAG, "Register failed", error)
                    AuthResult.Error(error.message ?: "Erro ao criar conta")
                }
            )
        }
    }

    private fun parseBirthDate(birthDate: String?): String? {
        if (birthDate.isNullOrBlank()) return null
        val digits = birthDate.filter { it.isDigit() }
        if (digits.length != 8) return null
        return "${digits.slice(4..7)}-${digits.slice(2..3)}-${digits.slice(0..1)}"
    }

    suspend fun logout() {
        sessionManager.clearSession()
        _authState.value = AuthState.Unauthenticated
        Log.d(TAG, "Logged out")
    }

    fun close() {
        httpClient.close()
    }
}
