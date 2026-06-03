package com.pedroaba.tccmobile.backend.online

import kotlin.test.Test
import kotlin.test.assertEquals

class StompWebSocketClientTest {

    @Test
    fun `parseFrames reads command headers and body`() {
        val frames = parseFrames(
            """
            MESSAGE
            destination:/topic/session/session-1/leaderboard
            subscription:leaderboard-session-1

            {"sessionId":"session-1","entries":[]}
            ${'\u0000'}
            """.trimIndent()
        )

        assertEquals(1, frames.size)
        assertEquals("MESSAGE", frames.first().command)
        assertEquals("/topic/session/session-1/leaderboard", frames.first().headers["destination"])
        assertEquals("leaderboard-session-1", frames.first().headers["subscription"])
        assertEquals("""{"sessionId":"session-1","entries":[]}""", frames.first().body)
    }

    @Test
    fun `parseFrames handles multiple messages`() {
        val frames = parseFrames(
            """
            MESSAGE
            destination:/topic/session/session-1/leaderboard

            {"type":"leaderboard"}
            ${'\u0000'}MESSAGE
            destination:/topic/session/session-1/game-state

            {"type":"game-state"}
            ${'\u0000'}
            """.trimIndent()
        )

        assertEquals(2, frames.size)
        assertEquals("/topic/session/session-1/leaderboard", frames[0].headers["destination"])
        assertEquals("""{"type":"leaderboard"}""", frames[0].body)
        assertEquals("/topic/session/session-1/game-state", frames[1].headers["destination"])
        assertEquals("""{"type":"game-state"}""", frames[1].body)
    }
}
