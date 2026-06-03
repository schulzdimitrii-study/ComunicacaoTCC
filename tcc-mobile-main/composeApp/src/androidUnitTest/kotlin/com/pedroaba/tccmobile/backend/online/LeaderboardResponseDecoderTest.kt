package com.pedroaba.tccmobile.backend.online

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class LeaderboardResponseDecoderTest {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Test
    fun `decodes backend list response and computes current user rank`() {
        val response = decodeLeaderboardResponse(
            json = json,
            sessionId = "session-1",
            currentUserId = "u2",
            responseBody = """
                [
                  {"userId":"u1","rank":1,"distanceKm":3.4},
                  {"userId":"u2","rank":2,"distanceKm":2.1}
                ]
            """.trimIndent()
        )

        assertEquals("session-1", response.sessionId)
        assertEquals(2, response.userRank)
        assertNull(response.hordeVirtualDistanceKm)
        assertEquals(2, response.entries.size)
        assertEquals("u1", response.entries.first().userId)
    }

    @Test
    fun `decodes full websocket shaped response`() {
        val response = decodeLeaderboardResponse(
            json = json,
            sessionId = "ignored-session",
            currentUserId = "u1",
            responseBody = """
                {
                  "sessionId":"session-2",
                  "userRank":1,
                  "hordeVirtualDistanceKm":1.25,
                  "isBehindHorde":false,
                  "distanceToHorde":-2.75,
                  "entries":[{"userId":"u1","rank":1,"distanceKm":4.0}]
                }
            """.trimIndent()
        )

        assertEquals("session-2", response.sessionId)
        assertEquals(1, response.userRank)
        assertEquals(1.25, response.hordeVirtualDistanceKm)
        assertEquals(false, response.isBehindHorde)
        assertEquals(-2.75, response.distanceToHorde)
        assertEquals(1, response.entries.size)
    }

    @Test
    fun `decodes empty list as empty leaderboard`() {
        val response = decodeLeaderboardResponse(
            json = json,
            sessionId = "session-1",
            currentUserId = "u1",
            responseBody = "[]"
        )

        assertEquals("session-1", response.sessionId)
        assertEquals(0, response.userRank)
        assertEquals(emptyList(), response.entries)
    }

    @Test
    fun `throws when leaderboard list item is malformed`() {
        assertFailsWith<SerializationException> {
            decodeLeaderboardResponse(
                json = json,
                sessionId = "session-1",
                currentUserId = "u1",
                responseBody = """[{"userId":"u1","rank":1}]"""
            )
        }
    }
}
