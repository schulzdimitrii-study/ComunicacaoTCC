package com.pedroaba.tccmobile.game

import com.pedroaba.tccmobile.game.models.SessionConfig
import com.pedroaba.tccmobile.game.telemetry.model.EscapeMetrics
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameControllerMovementTelemetryTest {
    @Test
    fun applyEscapeMetricsUpdatesSnapshotWhenSessionIsActive() {
        var nowMs = 0L
        val controller = GameController(timeProviderMs = { nowMs })
        controller.startSession(
            SessionConfig(
                initialDistance = 500.0,
                goalDistance = 1_000.0,
                escapeRatePerSecond = 10.0,
                chaseRatePerSecond = 4.0
            )
        )
        nowMs = 1_000L

        controller.applyEscapeMetrics(
            EscapeMetrics(
                timestampMs = 1_000L,
                strategy = TelemetryStrategy.MOVEMENT_ONLY,
                distanceMeters = 25.0,
                speedMetersPerSecond = 2.8,
                accelerationMetersPerSecondSquared = 0.6,
                normalizedDistance = 0.025,
                normalizedSpeed = 0.7,
                normalizedAcceleration = 0.24,
                movementScore = 0.8,
                finalScore = 0.8,
                biofeedbackPresent = false
            )
        )

        val snapshot = controller.snapshot.value
        assertTrue(snapshot.distance > 500.0)
        assertEquals(1.0, snapshot.elapsedSeconds, 0.0001)
        assertEquals(0.8, snapshot.performanceScore, 0.0001)
    }

    @Test
    fun applyEscapeMetricsIgnoresGameplayUpdatesThatArriveTooFast() {
        var nowMs = 0L
        val controller = GameController(timeProviderMs = { nowMs })
        controller.startSession(
            SessionConfig(
                initialDistance = 500.0,
                goalDistance = 1_000.0,
                escapeRatePerSecond = 10.0,
                chaseRatePerSecond = 4.0
            )
        )
        nowMs = 1_000L

        controller.applyEscapeMetrics(
            EscapeMetrics(
                timestampMs = 1_000L,
                strategy = TelemetryStrategy.MOVEMENT_ONLY,
                distanceMeters = 10.0,
                speedMetersPerSecond = 2.0,
                accelerationMetersPerSecondSquared = 0.5,
                normalizedDistance = 0.01,
                normalizedSpeed = 0.5,
                normalizedAcceleration = 0.2,
                movementScore = 0.7,
                finalScore = 0.7,
                biofeedbackPresent = false
            )
        )
        val firstSnapshot = controller.snapshot.value

        nowMs = 1_150L
        controller.applyEscapeMetrics(
            EscapeMetrics(
                timestampMs = 1_150L,
                strategy = TelemetryStrategy.MOVEMENT_ONLY,
                distanceMeters = 11.0,
                speedMetersPerSecond = 2.1,
                accelerationMetersPerSecondSquared = 0.55,
                normalizedDistance = 0.011,
                normalizedSpeed = 0.52,
                normalizedAcceleration = 0.22,
                movementScore = 0.72,
                finalScore = 0.72,
                biofeedbackPresent = false
            )
        )

        assertEquals(firstSnapshot, controller.snapshot.value)
    }

    @Test
    fun applyEscapeMetricsStopsSessionWhenPlayerEscapes() {
        var nowMs = 0L
        val controller = GameController(timeProviderMs = { nowMs })
        controller.startSession(
            SessionConfig(
                initialDistance = 999.0,
                goalDistance = 1_000.0,
                escapeRatePerSecond = 6.0,
                chaseRatePerSecond = 0.0
            )
        )
        nowMs = 1_000L

        controller.applyEscapeMetrics(
            EscapeMetrics(
                timestampMs = 1_000L,
                strategy = TelemetryStrategy.MOVEMENT_ONLY,
                distanceMeters = 20.0,
                speedMetersPerSecond = 3.2,
                accelerationMetersPerSecondSquared = 0.8,
                normalizedDistance = 0.02,
                normalizedSpeed = 0.8,
                normalizedAcceleration = 0.32,
                movementScore = 1.0,
                finalScore = 1.0,
                biofeedbackPresent = false
            )
        )

        assertEquals("escaped", controller.snapshot.value.result)
        assertEquals(false, controller.isActive.value)
    }

    @Test
    fun applyEscapeMetricsUsesDeviceTimerForElapsedSeconds() {
        var nowMs = 0L
        val controller = GameController(timeProviderMs = { nowMs })
        controller.startSession(
            SessionConfig(
                initialDistance = 500.0,
                goalDistance = 1_000.0,
                escapeRatePerSecond = 10.0,
                chaseRatePerSecond = 4.0
            )
        )

        nowMs = 1_000L
        controller.applyEscapeMetrics(
            EscapeMetrics(
                timestampMs = 10L,
                strategy = TelemetryStrategy.MOVEMENT_ONLY,
                distanceMeters = 10.0,
                speedMetersPerSecond = 2.0,
                accelerationMetersPerSecondSquared = 0.5,
                normalizedDistance = 0.01,
                normalizedSpeed = 0.5,
                normalizedAcceleration = 0.2,
                movementScore = 0.7,
                finalScore = 0.7,
                biofeedbackPresent = false
            )
        )

        nowMs = 2_000L
        controller.applyEscapeMetrics(
            EscapeMetrics(
                timestampMs = 9_999L,
                strategy = TelemetryStrategy.MOVEMENT_ONLY,
                distanceMeters = 12.0,
                speedMetersPerSecond = 2.2,
                accelerationMetersPerSecondSquared = 0.55,
                normalizedDistance = 0.012,
                normalizedSpeed = 0.55,
                normalizedAcceleration = 0.22,
                movementScore = 0.72,
                finalScore = 0.72,
                biofeedbackPresent = false
            )
        )

        assertEquals(2.0, controller.snapshot.value.elapsedSeconds, 0.0001)
    }
}
