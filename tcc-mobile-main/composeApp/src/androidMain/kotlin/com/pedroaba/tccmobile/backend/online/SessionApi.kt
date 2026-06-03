package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.http.BackendHttpClient
import com.pedroaba.tccmobile.backend.http.bearerAuth
import com.pedroaba.tccmobile.backend.http.safeApiCall
import com.pedroaba.tccmobile.backend.model.HordeDto
import com.pedroaba.tccmobile.backend.model.LeaderboardEntryDto
import com.pedroaba.tccmobile.backend.model.LeaderboardResponse
import com.pedroaba.tccmobile.backend.model.StartSessionRequest
import com.pedroaba.tccmobile.backend.model.StartSessionResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class SessionApi(
    private val backendHttpClient: BackendHttpClient,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
) {
    suspend fun startSession(
        token: String,
        request: StartSessionRequest = StartSessionRequest()
    ): Result<StartSessionResponse> = safeApiCall {
        backendHttpClient.client.post("/sessions/start") {
            bearerAuth(token)
            setBody(request)
        }.body()
    }

    suspend fun getHordes(token: String): Result<List<HordeDto>> = safeApiCall {
        val responseBody = backendHttpClient.client.get("/sessions/hordes") {
            bearerAuth(token)
        }.bodyAsText()

        decodeHordesResponse(responseBody)
    }

    suspend fun getLeaderboard(
        token: String,
        sessionId: String,
        currentUserId: String
    ): Result<LeaderboardResponse> = safeApiCall {
        val responseBody = backendHttpClient.client.get("/sessions/$sessionId/leaderboard") {
            bearerAuth(token)
        }.bodyAsText()

        decodeLeaderboardResponse(
            json = json,
            sessionId = sessionId,
            currentUserId = currentUserId,
            responseBody = responseBody
        )
    }

    suspend fun endSession(
        token: String,
        sessionId: String
    ): Result<Unit> = safeApiCall {
        backendHttpClient.client.post("/sessions/$sessionId/finish") {
            bearerAuth(token)
        }
        Unit
    }

    private fun decodeHordesResponse(responseBody: String): List<HordeDto> {
        val element = json.parseToJsonElement(responseBody)
        return when (element) {
            is JsonArray -> decodeHordeArray(element)
            is JsonObject -> decodeHordeObject(element)
            else -> emptyList()
        }
    }

    private fun decodeHordeObject(element: JsonObject): List<HordeDto> {
        val wrappedArray = listOf("hordes", "data", "items", "content")
            .firstNotNullOfOrNull { key ->
                element[key]?.let { it as? JsonArray }
            }

        return wrappedArray?.let(::decodeHordeArray)
            ?: listOf(json.decodeFromJsonElement(element))
    }

    private fun decodeHordeArray(element: JsonArray): List<HordeDto> =
        element.map { json.decodeFromJsonElement(it) }

}

internal fun decodeLeaderboardResponse(
    json: Json,
    sessionId: String,
    currentUserId: String,
    responseBody: String
): LeaderboardResponse {
    val element = json.parseToJsonElement(responseBody)
    return when (element) {
        is JsonArray -> {
            val entries = element.map { json.decodeFromJsonElement<LeaderboardEntryDto>(it) }
            LeaderboardResponse(
                sessionId = sessionId,
                userRank = entries.firstOrNull { it.userId == currentUserId }?.rank ?: 0,
                hordeVirtualDistanceKm = null,
                entries = entries
            )
        }

        is JsonObject -> json.decodeFromJsonElement(element)
        else -> LeaderboardResponse(
            sessionId = sessionId,
            userRank = 0,
            hordeVirtualDistanceKm = null,
            entries = emptyList()
        )
    }
}
