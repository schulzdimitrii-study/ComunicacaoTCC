package com.pedroaba.tccmobile.game.telemetry.usecase

import com.pedroaba.tccmobile.game.telemetry.model.AccelerationSample
import com.pedroaba.tccmobile.game.telemetry.model.LocationPoint
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryProcessorConfig
import com.pedroaba.tccmobile.game.telemetry.model.TelemetrySample
import kotlin.math.*

class MovementTelemetryProcessor(
    private val config: TelemetryProcessorConfig = TelemetryProcessorConfig()
) {
    private var lastAcceptedLocation: LocationPoint? = null
    private var totalDistanceMeters = 0.0
    private var smoothedSpeedMetersPerSecond = 0.0
    private var smoothedDerivedAccelerationMetersPerSecondSquared = 0.0
    private var smoothedRawAccelerationMetersPerSecondSquared: Double? = null
    private var lastTelemetryTimestampMs: Long? = null

    fun reset() {
        lastAcceptedLocation = null
        totalDistanceMeters = 0.0
        smoothedSpeedMetersPerSecond = 0.0
        smoothedDerivedAccelerationMetersPerSecondSquared = 0.0
        smoothedRawAccelerationMetersPerSecondSquared = null
        lastTelemetryTimestampMs = null
    }

    fun onLocation(point: LocationPoint): TelemetrySample? {
        if (!isAccepted(point)) return null

        val previous = lastAcceptedLocation
        val deltaSeconds = previous?.let { ((point.timestampMs - it.timestampMs) / 1000.0).takeIf { seconds -> seconds > 0.0 } } ?: 0.0
        val rawDistanceDelta = previous?.let { distanceBetweenMeters(it, point) } ?: 0.0
        if (isImplausibleJump(rawDistanceDelta, deltaSeconds)) return null
        val acceptedDistanceDelta = if (rawDistanceDelta >= config.minDistanceDeltaMeters) rawDistanceDelta else 0.0
        totalDistanceMeters += acceptedDistanceDelta

        val candidateSpeed = preferredSpeed(point, acceptedDistanceDelta, deltaSeconds)
        val previousSpeed = smoothedSpeedMetersPerSecond
        smoothedSpeedMetersPerSecond = smooth(
            previous = smoothedSpeedMetersPerSecond,
            newValue = candidateSpeed,
            factor = config.speedSmoothingFactor
        )

        val derivedAcceleration = if (deltaSeconds > 0.0) {
            ((smoothedSpeedMetersPerSecond - previousSpeed) / deltaSeconds)
                .coerceIn(
                    -config.maxAcceptedAccelerationMetersPerSecondSquared,
                    config.maxAcceptedAccelerationMetersPerSecondSquared
                )
        } else {
            0.0
        }
        smoothedDerivedAccelerationMetersPerSecondSquared = smooth(
            previous = smoothedDerivedAccelerationMetersPerSecondSquared,
            newValue = derivedAcceleration,
            factor = config.accelerationSmoothingFactor
        )

        lastAcceptedLocation = point
        lastTelemetryTimestampMs = point.timestampMs
        return buildSample(
            timestampMs = point.timestampMs,
            locationPoint = point,
            distanceDeltaMeters = acceptedDistanceDelta
        )
    }

    fun onAcceleration(sample: AccelerationSample): TelemetrySample? {
        smoothedRawAccelerationMetersPerSecondSquared = smoothNullable(
            previous = smoothedRawAccelerationMetersPerSecondSquared,
            newValue = sample.magnitudeMetersPerSecondSquared,
            factor = config.rawAccelerationSmoothingFactor
        )
        lastTelemetryTimestampMs = sample.timestampMs

        return buildSample(
            timestampMs = sample.timestampMs,
            locationPoint = lastAcceptedLocation,
            distanceDeltaMeters = 0.0
        )
    }

    fun snapshotAt(timestampMs: Long): TelemetrySample? {
        val lastTimestamp = lastTelemetryTimestampMs ?: return null
        val isStale = timestampMs - lastTimestamp >= config.staleLocationThresholdMs

        if (isStale) {
            smoothedSpeedMetersPerSecond = 0.0
            smoothedDerivedAccelerationMetersPerSecondSquared = 0.0
        }

        return buildSample(
            timestampMs = timestampMs,
            locationPoint = lastAcceptedLocation,
            distanceDeltaMeters = 0.0
        )
    }

    private fun buildSample(
        timestampMs: Long,
        locationPoint: LocationPoint?,
        distanceDeltaMeters: Double
    ): TelemetrySample? {
        if (locationPoint == null && smoothedRawAccelerationMetersPerSecondSquared == null) return null

        val effectiveAcceleration = if (totalDistanceMeters > 0.0) {
            smoothedDerivedAccelerationMetersPerSecondSquared
        } else {
            smoothedRawAccelerationMetersPerSecondSquared
                ?: smoothedDerivedAccelerationMetersPerSecondSquared
        }
        val isLocationStale = lastTelemetryTimestampMs?.let {
            timestampMs - it >= config.staleLocationThresholdMs
        } ?: false
        val isMoving = !isLocationStale && (
            smoothedSpeedMetersPerSecond >= config.minMovingSpeedMetersPerSecond ||
                abs(effectiveAcceleration) >= config.movementAccelerationThresholdMetersPerSecondSquared
            )
        val movementConfidence = computeConfidence(locationPoint, isLocationStale, isMoving)

        return TelemetrySample(
            timestampMs = timestampMs,
            totalDistanceMeters = totalDistanceMeters,
            distanceDeltaMeters = distanceDeltaMeters,
            speedMetersPerSecond = smoothedSpeedMetersPerSecond,
            derivedAccelerationMetersPerSecondSquared = smoothedDerivedAccelerationMetersPerSecondSquared,
            rawAccelerationMetersPerSecondSquared = smoothedRawAccelerationMetersPerSecondSquared,
            effectiveAccelerationMetersPerSecondSquared = effectiveAcceleration,
            movementConfidence = movementConfidence,
            signalQuality = computeSignalQuality(locationPoint, isLocationStale),
            isMoving = isMoving,
            isLocationStale = isLocationStale,
            locationPoint = locationPoint
        )
    }

    private fun preferredSpeed(
        point: LocationPoint,
        distanceDeltaMeters: Double,
        deltaSeconds: Double
    ): Double {
        val gpsSpeed = point.speedMetersPerSecond
            ?.takeIf { it in 0.0..config.maxAcceptedSpeedMetersPerSecond }
            ?.takeIf {
                point.speedAccuracyMetersPerSecond == null ||
                    point.speedAccuracyMetersPerSecond <= config.maxAcceptedSpeedAccuracyMetersPerSecond
            }

        val derivedSpeed = if (deltaSeconds > 0.0) {
            (distanceDeltaMeters / deltaSeconds).coerceAtMost(config.maxAcceptedSpeedMetersPerSecond)
        } else {
            0.0
        }

        return gpsSpeed ?: derivedSpeed
    }

    private fun computeConfidence(
        locationPoint: LocationPoint?,
        isLocationStale: Boolean,
        isMoving: Boolean
    ): Double {
        val accuracyScore = when (val accuracy = locationPoint?.accuracyMeters) {
            null -> 0.6
            in 0.0..10.0 -> 1.0
            in 10.0..20.0 -> 0.85
            in 20.0..35.0 -> 0.65
            else -> 0.35
        }
        val accelerationBonus = if (smoothedRawAccelerationMetersPerSecondSquared != null) 0.1 else 0.0
        val stalePenalty = if (isLocationStale) 0.0 else 1.0
        val movingBonus = if (isMoving) 0.1 else 0.0
        return ((accuracyScore + accelerationBonus + movingBonus) * stalePenalty).coerceIn(0.0, 1.0)
    }

    private fun computeSignalQuality(locationPoint: LocationPoint?, isLocationStale: Boolean): Double {
        val accuracy = locationPoint?.accuracyMeters ?: return if (isLocationStale) 0.2 else 0.6
        val base = (1.0 - (accuracy / config.maxAcceptedAccuracyMeters)).coerceIn(0.1, 1.0)
        return if (isLocationStale) (base * 0.25).coerceIn(0.0, 1.0) else base
    }

    private fun isAccepted(point: LocationPoint): Boolean {
        val accuracy = point.accuracyMeters ?: return true
        return accuracy <= config.maxAcceptedAccuracyMeters
    }

    private fun isImplausibleJump(distanceDeltaMeters: Double, deltaSeconds: Double): Boolean {
        if (distanceDeltaMeters > config.maxAcceptedDistanceDeltaMeters) return true
        if (deltaSeconds <= 0.0) return false
        return (distanceDeltaMeters / deltaSeconds) > config.maxAcceptedSpeedMetersPerSecond
    }

    private fun smooth(previous: Double, newValue: Double, factor: Double): Double {
        return previous + (newValue - previous) * factor.coerceIn(0.0, 1.0)
    }

    private fun smoothNullable(previous: Double?, newValue: Double, factor: Double): Double {
        return previous?.let { smooth(it, newValue, factor) } ?: newValue
    }

    private fun distanceBetweenMeters(start: LocationPoint, end: LocationPoint): Double {
        val earthRadiusMeters = 6_371_000.0
        val lat1 = start.latitude.toRadians()
        val lat2 = end.latitude.toRadians()
        val deltaLat = (end.latitude - start.latitude).toRadians()
        val deltaLon = (end.longitude - start.longitude).toRadians()

        val a = sin(deltaLat / 2.0).pow(2) +
            cos(lat1) * cos(lat2) * sin(deltaLon / 2.0).pow(2)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return earthRadiusMeters * c
    }

    private fun Double.toRadians(): Double = this * PI / 180.0
}
