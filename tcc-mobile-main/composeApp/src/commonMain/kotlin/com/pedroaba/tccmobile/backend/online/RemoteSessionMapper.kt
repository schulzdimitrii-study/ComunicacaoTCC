package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.model.BiometricDataMessage
import com.pedroaba.tccmobile.game.models.GameSnapshot
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import kotlin.math.round
import kotlin.math.roundToInt

fun buildBiometricDataMessage(
    sessionId: String,
    userId: String,
    telemetryState: TelemetryState,
    snapshot: GameSnapshot,
    timestampMs: Long
): BiometricDataMessage? {
    val sample = telemetryState.latestSample ?: return null
    val speedKmH = roundToTwoDecimals(sample.speedMetersPerSecond * 3.6)
    val pace = if (speedKmH > 0.0) roundToOneDecimal(60.0 / speedKmH) else 0.0
    val distanceKm = roundToTwoDecimals(sample.totalDistanceMeters / 1_000.0)
    val estimatedCadence = roundToOneDecimal(sample.speedMetersPerSecond * 26.4705882353)
    val estimatedCalories = roundToOneDecimal(distanceKm * 150.0)

    return BiometricDataMessage(
        sessionId = sessionId,
        userId = userId,
        timestamp = timestampMs,
        bpm = telemetryState.latestBiofeedbackSample?.bpm ?: estimateBpm(snapshot, speedKmH),
        cadence = estimatedCadence,
        speed = speedKmH,
        pace = pace,
        accumulatedDistance = distanceKm,
        accumulatedCalories = if (estimatedCalories > 0.0) {
            estimatedCalories
        } else {
            roundToOneDecimal(snapshot.distance * 0.15)
        }
    )
}

private fun roundToOneDecimal(value: Double): Double = round(value * 10.0) / 10.0

private fun roundToTwoDecimals(value: Double): Double = round(value * 100.0) / 100.0

private fun estimateBpm(snapshot: GameSnapshot, speedKmH: Double): Int {
    val effortFromPerformance = ((1.0 - snapshot.performanceScore.coerceIn(0.0, 1.0)) * 35.0)
    val effortFromSpeed = (speedKmH * 3.0).coerceIn(0.0, 45.0)
    return (95.0 + effortFromPerformance + effortFromSpeed)
        .roundToInt()
        .coerceIn(1, 220)
}
