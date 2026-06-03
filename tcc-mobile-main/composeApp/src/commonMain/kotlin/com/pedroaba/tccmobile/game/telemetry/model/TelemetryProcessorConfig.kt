package com.pedroaba.tccmobile.game.telemetry.model

data class TelemetryProcessorConfig(
    val minDistanceDeltaMeters: Double = 2.0,
    val maxAcceptedDistanceDeltaMeters: Double = 45.0,
    val maxAcceptedAccuracyMeters: Double = 35.0,
    val maxAcceptedSpeedMetersPerSecond: Double = 8.5,
    val maxAcceptedSpeedAccuracyMetersPerSecond: Double = 2.5,
    val maxAcceptedAccelerationMetersPerSecondSquared: Double = 4.0,
    val minMovingSpeedMetersPerSecond: Double = 0.8,
    val movementAccelerationThresholdMetersPerSecondSquared: Double = 0.35,
    val staleLocationThresholdMs: Long = 6_000L,
    val speedSmoothingFactor: Double = 0.35,
    val accelerationSmoothingFactor: Double = 0.25,
    val rawAccelerationSmoothingFactor: Double = 0.2
)
