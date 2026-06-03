package com.pedroaba.tccmobile.game

import com.pedroaba.tccmobile.game.models.BiofeedbackSample
import com.pedroaba.tccmobile.game.models.SessionConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameControllerTest {
    @Test
    fun testSceneLoadingStateCanBeUpdated() {
        val controller = GameController()

        assertTrue(controller.isSceneLoading.value)

        controller.onSceneLoaded()

        assertFalse(controller.isSceneLoading.value)

        controller.onSceneLoadingStarted()

        assertTrue(controller.isSceneLoading.value)
    }

    @Test
    fun testStartSessionInitializesSnapshot() {
        val controller = GameController()
        val config = SessionConfig(initialDistance = 500.0)
        
        controller.startSession(config)
        
        val snapshot = controller.snapshot.value
        assertEquals(500.0, snapshot.distance)
        assertEquals("running", snapshot.result)
    }

    @Test
    fun testSendBiofeedbackUpdatesSnapshot() {
        val controller = GameController()
        val config = SessionConfig(
            initialDistance = 500.0,
            goalDistance = 1000.0,
            targetMinBpm = 100,
            targetMaxBpm = 120,
            cadenceMin = 160,
            cadenceMax = 180,
            escapeRatePerSecond = 10.0,
            chaseRatePerSecond = 5.0
        )
        controller.startSession(config)
        
        // Sample within targets
        val sample = BiofeedbackSample(bpm = 110, cadence = 170, timestampMs = 1000)
        controller.sendBiofeedback(sample)
        
        val snapshot = controller.snapshot.value
        // performanceScore should be high, runnerVelocity near 10.0, hordeVelocity near 0.0
        // distance should be 500.0 + (10.0 - 0.0) * 1.0 = 510.0
        assertTrue(snapshot.distance > 500.0)
        assertEquals(1.0, snapshot.elapsedSeconds)
    }

    @Test
    fun testSessionStopsOnEscaped() {
        val controller = GameController()
        val config = SessionConfig(
            initialDistance = 995.0,
            goalDistance = 1000.0,
            escapeRatePerSecond = 10.0,
            chaseRatePerSecond = 0.0
        )
        controller.startSession(config)
        
        val sample = BiofeedbackSample(bpm = 110, cadence = 170, timestampMs = 1000)
        controller.sendBiofeedback(sample) // Should move > 5m and escape
        
        val snapshot = controller.snapshot.value
        assertEquals("escaped", snapshot.result)
        
        // Further biofeedback should not update snapshot
        controller.sendBiofeedback(sample)
        assertEquals(snapshot, controller.snapshot.value)
    }

    @Test
    fun testSessionStopsOnCaught() {
        val controller = GameController()
        val config = SessionConfig(
            initialDistance = 5.0,
            goalDistance = 1000.0,
            escapeRatePerSecond = 0.0,
            chaseRatePerSecond = 10.0
        )
        controller.startSession(config)
        
        // High heart rate, low performance, high horde velocity
        val sample = BiofeedbackSample(bpm = 200, cadence = 50, timestampMs = 1000)
        controller.sendBiofeedback(sample)
        
        val snapshot = controller.snapshot.value
        assertEquals("caught", snapshot.result)
        
        // Further biofeedback should not update snapshot
        controller.sendBiofeedback(sample)
        assertEquals(snapshot, controller.snapshot.value)
    }
}
