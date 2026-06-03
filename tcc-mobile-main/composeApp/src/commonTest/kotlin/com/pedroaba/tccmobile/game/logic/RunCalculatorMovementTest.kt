package com.pedroaba.tccmobile.game.logic

import com.pedroaba.tccmobile.game.models.SessionConfig
import com.pedroaba.tccmobile.game.telemetry.model.EscapeMetrics
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RunCalculatorMovementTest {
    @Test
    fun calculateSnapshotFromEscapeMetricsUsesMovementScoreAsPerformance() {
        val calculator = RunCalculator()
        val snapshot = calculator.calculateSnapshotFromEscapeMetrics(
            currentDistance = 500.0,
            elapsedSeconds = 0.0,
            deltaSeconds = 2.0,
            metrics = EscapeMetrics(
                timestampMs = 2_000L,
                strategy = TelemetryStrategy.MOVEMENT_ONLY,
                distanceMeters = 120.0,
                speedMetersPerSecond = 3.0,
                accelerationMetersPerSecondSquared = 0.9,
                normalizedDistance = 0.12,
                normalizedSpeed = 0.75,
                normalizedAcceleration = 0.36,
                movementScore = 0.7,
                finalScore = 0.7,
                biofeedbackPresent = false
            ),
            config = SessionConfig(
                escapeRatePerSecond = 10.0,
                chaseRatePerSecond = 6.0
            )
        )

        assertEquals(0.7, snapshot.performanceScore, 0.0001)
        assertTrue(snapshot.distance > 500.0)
        assertEquals(2.0, snapshot.elapsedSeconds, 0.0001)
    }
}
