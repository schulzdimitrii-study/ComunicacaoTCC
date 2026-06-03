package com.pedroaba.tccmobile.auth.model

import android.util.Base64
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

@Serializable
data class UserSession(
    val token: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = ""
) {
    fun isValid(): Boolean =
        token.isNotBlank() && userId.isNotBlank() && !isJwtExpired(token)
}

private fun isJwtExpired(rawToken: String): Boolean {
    val token = rawToken.removePrefix("Bearer ").removePrefix("bearer ").trim()
    val payload = token.split(".").getOrNull(1) ?: return false

    return runCatching {
        val decodedPayload = String(Base64.decode(payload, Base64.URL_SAFE), Charsets.UTF_8)
        val expirationSeconds = Json.parseToJsonElement(decodedPayload)
            .jsonObject["exp"]
            ?.jsonPrimitive
            ?.longOrNull
            ?: return false

        expirationSeconds <= System.currentTimeMillis() / 1_000
    }.getOrDefault(false)
}
