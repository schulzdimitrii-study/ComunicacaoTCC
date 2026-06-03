package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.model.HordeDto
import com.pedroaba.tccmobile.game.models.SessionConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HordeSessionConfigTest {

    @Test
    fun `hard horde with fast pace increases chase pressure`() {
        val config = HordeDto(
            id = "horde-1",
            name = "Industrial",
            difficulty = "HARD",
            estimatedDuration = 45,
            targetPace = 5.0
        ).toSessionConfig()

        assertEquals(45 * 60.0, config.sessionDurationSeconds)
        assertTrue(config.chaseRatePerSecond > SessionConfig().chaseRatePerSecond)
    }

    @Test
    fun `easy horde with slow pace lowers chase pressure`() {
        val config = HordeDto(
            id = "horde-1",
            name = "Centro",
            difficulty = "EASY",
            estimatedDuration = 20,
            targetPace = 8.0
        ).toSessionConfig()

        assertEquals(20 * 60.0, config.sessionDurationSeconds)
        assertTrue(config.chaseRatePerSecond < SessionConfig().chaseRatePerSecond)
    }

    @Test
    fun `invalid duration falls back to default session duration`() {
        val config = HordeDto(
            id = "horde-1",
            name = "Livre",
            estimatedDuration = 0
        ).toSessionConfig()

        assertEquals(SessionConfig().sessionDurationSeconds, config.sessionDurationSeconds)
    }

    @Test
    fun `display helpers return localized labels and fallbacks`() {
        val horde = HordeDto(
            id = "horde-1",
            name = "Livre",
            difficulty = "UNKNOWN",
            estimatedDuration = 0,
            targetPace = null
        )

        assertEquals("Média", horde.displayDifficulty())
        assertEquals("sem pace alvo", horde.displayPace())
        assertEquals("duracao livre", horde.displayDuration())
    }
}
