package com.pedroaba.tccmobile.auth.http

import com.pedroaba.tccmobile.auth.model.AuthResponse
import com.pedroaba.tccmobile.auth.model.LoginRequest
import com.pedroaba.tccmobile.auth.model.RegisterRequest
import com.pedroaba.tccmobile.backend.http.BackendHttpClient
import com.pedroaba.tccmobile.backend.http.safeApiCall
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthHttpClient(
    private val backendHttpClient: BackendHttpClient = BackendHttpClient()
) {

    suspend fun login(request: LoginRequest): Result<AuthResponse> = safeApiCall {
        backendHttpClient.client.post("/auth/login") {
            setBody(request)
        }.body()
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> = safeApiCall {
        backendHttpClient.client.post("/auth/register") {
            setBody(request)
        }.body()
    }

    fun close() {
        backendHttpClient.close()
    }
}
