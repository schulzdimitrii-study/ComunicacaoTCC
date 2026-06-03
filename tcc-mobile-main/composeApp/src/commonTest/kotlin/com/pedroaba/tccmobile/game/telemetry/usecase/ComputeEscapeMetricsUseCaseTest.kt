package com.pedroaba.tccmobile.game.telemetry.usecase

import com.pedroaba.tccmobile.game.telemetry.model.EscapeMetricsConfig
import com.pedroaba.tccmobile.game.telemetry.model.TelemetrySample
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComputeEscapeMetricsUseCaseTest {
    private val useCase = ComputeEscapeMetricsUseCase()

    @Test
    fun returnsZeroedMetricsWhenSampleIsMissing() {
        val metrics = useCase(
            sample = null,
            strategy = TelemetryStrategy.MOVEMENT_ONLY
        )

        assertEquals(0.0, metrics.movementScore)
        assertEquals(0.0, metrics.finalScore)
        assertEquals(0.0, metrics.normalizedDistance)
        assertEquals(0.0, metrics.normalizedSpeed)
        assertEquals(0.0, metrics.normalizedAcceleration)
    }

    @Test
    fun normalizesMovementSignalsIntoFutureFriendlyScore() {
        val metrics = useCase(
            sample = TelemetrySample(
                timestampMs = 2_000L,
                totalDistanceMeters = 500.0,
                distanceDeltaMeters = 12.0,
                speedMetersPerSecond = 3.0,
                derivedAccelerationMetersPerSecondSquared = 1.2,
                rawAccelerationMetersPerSecondSquared = 0.9,
                effectiveAccelerationMetersPerSecondSquared = 1.2,
                movementConfidence = 0.85,
                signalQuality = 0.85,
                isMoving = true,
                isLocationStale = false
            ),
            strategy = TelemetryStrategy.MOVEMENT_ONLY,
            config = EscapeMetricsConfig(
                distanceReferenceMeters = 1_000.0,
                speedReferenceMetersPerSecond = 4.0,
                accelerationReferenceMetersPerSecondSquared = 2.0
            )
        )

        assertEquals(0.5, metrics.normalizedDistance, 0.0001)
        assertEquals(0.75, metrics.normalizedSpeed, 0.0001)
        assertEquals(0.6, metrics.normalizedAcceleration, 0.0001)
        assertTrue(metrics.movementScore > 0.0)
        assertTrue(metrics.finalScore < metrics.movementScore)
    }

    @Test
    fun collapsesFinalScoreWhenSignalIsStaleAndNotMoving() {
        val metrics = useCase(
            sample = TelemetrySample(
                timestampMs = 5_000L,
                totalDistanceMeters = 80.0,
                distanceDeltaMeters = 0.0,
                speedMetersPerSecond = 0.0,
                derivedAccelerationMetersPerSecondSquared = 0.0,
                effectiveAccelerationMetersPerSecondSquared = 0.0,
                movementConfidence = 0.2,
                signalQuality = 0.1,
                isMoving = false,
                isLocationStale = true
            ),
            strategy = TelemetryStrategy.MOVEMENT_ONLY
        )

        assertTrue(metrics.movementScore >= 0.0)
        assertEquals(0.0, metrics.finalScore, 0.0001)
    }
}
