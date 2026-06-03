package com.pedroaba.tccmobile.game.telemetry.usecase

import com.pedroaba.tccmobile.game.telemetry.model.MovementSession
import com.pedroaba.tccmobile.game.telemetry.model.TelemetrySample
import com.pedroaba.tccmobile.game.telemetry.model.TelemetrySessionStatus
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import kotlin.test.Test
import kotlin.test.assertEquals

class TelemetryNotificationFormatterTest {
    private val formatter = TelemetryNotificationFormatter()

    @Test
    fun formatsForegroundNotificationContentFromTelemetryState() {
        val content = formatter.format(
            TelemetryState(
                session = MovementSession(
                    status = TelemetrySessionStatus.RUNNING,
                    totalDistanceMeters = 142.0
                ),
                latestSample = TelemetrySample(
                    timestampMs = 1_000L,
                    totalDistanceMeters = 142.0,
                    distanceDeltaMeters = 5.0,
                    speedMetersPerSecond = 2.75,
                    derivedAccelerationMetersPerSecondSquared = 0.8,
                    effectiveAccelerationMetersPerSecondSquared = 0.8,
                    movementConfidence = 0.9
                )
            )
        )

        assertEquals("Session running", content.title)
        assertEquals("Speed 2.8 m/s • Distance 142 m", content.body)
    }
}
