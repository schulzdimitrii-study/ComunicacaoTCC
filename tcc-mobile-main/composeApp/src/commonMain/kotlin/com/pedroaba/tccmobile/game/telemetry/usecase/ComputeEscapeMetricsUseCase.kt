package com.pedroaba.tccmobile.game.telemetry.usecase

import com.pedroaba.tccmobile.game.telemetry.model.EscapeMetrics
import com.pedroaba.tccmobile.game.telemetry.model.EscapeMetricsConfig
import com.pedroaba.tccmobile.game.telemetry.model.TelemetrySample
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryStrategy
import kotlin.math.abs

class ComputeEscapeMetricsUseCase {
    operator fun invoke(
        sample: TelemetrySample?,
        strategy: TelemetryStrategy,
        config: EscapeMetricsConfig = EscapeMetricsConfig(),
        biofeedbackPresent: Boolean = false
    ): EscapeMetrics {
        if (sample == null) {
            return EscapeMetrics(
                timestampMs = 0L,
                strategy = strategy,
                distanceMeters = 0.0,
                speedMetersPerSecond = 0.0,
                accelerationMetersPerSecondSquared = 0.0,
                normalizedDistance = 0.0,
                normalizedSpeed = 0.0,
                normalizedAcceleration = 0.0,
                movementScore = 0.0,
                finalScore = 0.0,
                biofeedbackPresent = biofeedbackPresent
            )
        }

        val normalizedDistance = normalize(sample.totalDistanceMeters, config.distanceReferenceMeters)
        val normalizedSpeed = normalize(sample.speedMetersPerSecond, config.speedReferenceMetersPerSecond)
        val normalizedAcceleration = normalize(
            abs(sample.effectiveAccelerationMetersPerSecondSquared),
            config.accelerationReferenceMetersPerSecondSquared
        )

        val movementScore = (
            normalizedDistance * 0.35 +
                normalizedSpeed * 0.45 +
                normalizedAcceleration * 0.20
            ).coerceIn(0.0, 1.0)

        val confidenceFactor = sample.movementConfidence.coerceIn(0.0, 1.0)
        val signalFactor = sample.signalQuality.coerceIn(0.0, 1.0)
        val staleFactor = if (sample.isLocationStale) 0.0 else 1.0
        val movingFactor = if (sample.isMoving) 1.0 else 0.0
        val finalScore = (movementScore * confidenceFactor * signalFactor * staleFactor * movingFactor)
            .coerceIn(0.0, 1.0)

        return EscapeMetrics(
            timestampMs = sample.timestampMs,
            strategy = strategy,
            distanceMeters = sample.totalDistanceMeters,
            speedMetersPerSecond = sample.speedMetersPerSecond,
            accelerationMetersPerSecondSquared = sample.effectiveAccelerationMetersPerSecondSquared,
            normalizedDistance = normalizedDistance,
            normalizedSpeed = normalizedSpeed,
            normalizedAcceleration = normalizedAcceleration,
            movementScore = movementScore,
            finalScore = finalScore,
            biofeedbackPresent = biofeedbackPresent
        )
    }

    private fun normalize(value: Double, reference: Double): Double {
        if (reference <= 0.0) return 0.0
        return (value / reference).coerceIn(0.0, 1.0)
    }
}
