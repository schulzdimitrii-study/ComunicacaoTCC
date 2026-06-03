package com.pedroaba.tccmobile.game.telemetry.model

data class LocationPoint(
    val timestampMs: Long,
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Double? = null,
    val altitudeMeters: Double? = null,
    val bearingDegrees: Double? = null,
    val speedMetersPerSecond: Double? = null,
    val speedAccuracyMetersPerSecond: Double? = null,
    val provider: String = "unknown"
)
