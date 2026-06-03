package com.pedroaba.tccmobile.game.visual

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DistanceVisualMapperTest {
    @Test
    fun mapsZeroDistanceToMinimumX() {
        val x = DistanceVisualMapper.runnerX(
            distance = 0.0,
            goalDistance = 1000.0,
            minX = 132.0,
            maxX = 380.0
        )

        assertEquals(132.0, x)
    }

    @Test
    fun mapsGoalDistanceToMaximumX() {
        val x = DistanceVisualMapper.runnerX(
            distance = 1000.0,
            goalDistance = 1000.0,
            minX = 132.0,
            maxX = 380.0
        )

        assertEquals(380.0, x)
    }

    @Test
    fun lowerDistancePushesRunnerFurtherLeft() {
        val farX = DistanceVisualMapper.runnerX(
            distance = 800.0,
            goalDistance = 1000.0,
            minX = 132.0,
            maxX = 380.0
        )
        val nearX = DistanceVisualMapper.runnerX(
            distance = 200.0,
            goalDistance = 1000.0,
            minX = 132.0,
            maxX = 380.0
        )

        assertTrue(nearX < farX)
    }
}
