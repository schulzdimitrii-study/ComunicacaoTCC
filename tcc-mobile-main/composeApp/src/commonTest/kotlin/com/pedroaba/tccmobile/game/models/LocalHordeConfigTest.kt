package com.pedroaba.tccmobile.game.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalHordeConfigTest {

    @Test
    fun `distance in km becomes goal distance in meters`() {
        val config = LocalHordeConfig(distanceKm = 2.5).toSessionConfig()

        assertEquals(2_500.0, config.goalDistance)
    }

    @Test
    fun `hard difficulty increases horde speed`() {
        val config = LocalHordeConfig(difficulty = LocalHordeDifficulty.HARD).toSessionConfig()

        assertTrue(config.chaseRatePerSecond > SessionConfig().chaseRatePerSecond)
    }

    @Test
    fun `easy difficulty lowers horde speed`() {
        val config = LocalHordeConfig(difficulty = LocalHordeDifficulty.EASY).toSessionConfig()

        assertTrue(config.chaseRatePerSecond < SessionConfig().chaseRatePerSecond)
    }
}
