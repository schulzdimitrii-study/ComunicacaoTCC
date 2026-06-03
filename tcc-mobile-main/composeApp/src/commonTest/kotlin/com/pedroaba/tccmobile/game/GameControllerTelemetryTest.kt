package com.pedroaba.tccmobile.game

import com.pedroaba.tccmobile.game.telemetry.model.EscapeMetrics
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryStrategy
import kotlin.test.Test
import kotlin.test.assertEquals

class GameControllerTelemetryTest {
    @Test
    fun storesLatestEscapeMetricsForFutureGameConsumption() {
        val controller = GameController()
        val metrics = EscapeMetrics(
            timestampMs = 4_000L,
            strategy = TelemetryStrategy.MOVEMENT_ONLY,
            distanceMeters = 120.0,
            speedMetersPerSecond = 2.8,
            accelerationMetersPerSecondSquared = 0.7,
            normalizedDistance = 0.12,
            normalizedSpeed = 0.56,
            normalizedAcceleration = 0.35,
            movementScore = 0.41,
            finalScore = 0.41,
            biofeedbackPresent = false
        )

        controller.updateEscapeMetrics(metrics)

        assertEquals(metrics, controller.lastEscapeMetrics.value)
    }
}
