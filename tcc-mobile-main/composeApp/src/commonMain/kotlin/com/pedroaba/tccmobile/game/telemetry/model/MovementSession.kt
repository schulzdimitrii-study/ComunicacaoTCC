package com.pedroaba.tccmobile.game.telemetry.model

data class MovementSession(
    val sessionId: String = "",
    val status: TelemetrySessionStatus = TelemetrySessionStatus.IDLE,
    val startedAtEpochMs: Long? = null,
    val lastUpdatedAtEpochMs: Long? = null,
    val activeDurationMs: Long = 0L,
    val totalDistanceMeters: Double = 0.0,
    val sampleCount: Int = 0
)
