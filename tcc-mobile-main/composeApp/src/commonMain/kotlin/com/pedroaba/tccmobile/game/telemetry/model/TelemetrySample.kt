package com.pedroaba.tccmobile.game.telemetry.model

data class TelemetrySample(
    val timestampMs: Long,
    val totalDistanceMeters: Double,
    val distanceDeltaMeters: Double,
    val speedMetersPerSecond: Double,
    val derivedAccelerationMetersPerSecondSquared: Double,
    val rawAccelerationMetersPerSecondSquared: Double? = null,
    val effectiveAccelerationMetersPerSecondSquared: Double,
    val movementConfidence: Double,
    val signalQuality: Double = movementConfidence,
    val isMoving: Boolean = false,
    val isLocationStale: Boolean = false,
    val locationPoint: LocationPoint? = null
)
