package com.pedroaba.tccmobile.game.telemetry.model

data class EscapeMetrics(
    val timestampMs: Long,
    val strategy: TelemetryStrategy,
    val distanceMeters: Double,
    val speedMetersPerSecond: Double,
    val accelerationMetersPerSecondSquared: Double,
    val normalizedDistance: Double,
    val normalizedSpeed: Double,
    val normalizedAcceleration: Double,
    val movementScore: Double,
    val finalScore: Double,
    val biofeedbackPresent: Boolean
)

data class EscapeMetricsConfig(
    val distanceReferenceMeters: Double = 1_000.0,
    val speedReferenceMetersPerSecond: Double = 4.0,
    val accelerationReferenceMetersPerSecondSquared: Double = 2.5
)
