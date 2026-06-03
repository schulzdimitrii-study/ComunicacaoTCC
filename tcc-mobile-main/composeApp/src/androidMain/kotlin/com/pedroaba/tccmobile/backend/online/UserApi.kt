package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.http.BackendHttpClient
import com.pedroaba.tccmobile.backend.http.bearerAuth
import com.pedroaba.tccmobile.backend.http.safeApiCall
import com.pedroaba.tccmobile.backend.model.UpdateUserProfileRequest
import com.pedroaba.tccmobile.backend.model.UserProfileDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody

class UserApi(
    private val backendHttpClient: BackendHttpClient
) {
    suspend fun getUser(token: String, userId: String): Result<UserProfileDto> = safeApiCall {
        backendHttpClient.client.get("/users/$userId") {
            bearerAuth(token)
        }.body()
    }

    suspend fun updateUser(
        token: String,
        userId: String,
        request: UpdateUserProfileRequest
    ): Result<UserProfileDto> = safeApiCall {
        backendHttpClient.client.patch("/users/$userId") {
            bearerAuth(token)
            setBody(request)
        }.body()
    }
}
