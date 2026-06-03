package com.pedroaba.tccmobile.game.telemetry.model

data class AccelerationSample(
    val timestampMs: Long,
    val magnitudeMetersPerSecondSquared: Double,
    val sensorType: MotionSensorType
)

enum class MotionSensorType {
    LINEAR_ACCELERATION,
    ACCELEROMETER,
    DERIVED
}
