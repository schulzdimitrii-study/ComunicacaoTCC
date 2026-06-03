package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.model.BiometricDataMessage
import com.pedroaba.tccmobile.game.models.GameSnapshot
import com.pedroaba.tccmobile.game.telemetry.model.BiofeedbackSample
import com.pedroaba.tccmobile.game.telemetry.model.MovementSession
import com.pedroaba.tccmobile.game.telemetry.model.TelemetrySessionStatus
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import com.pedroaba.tccmobile.game.telemetry.model.TelemetrySample
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RemoteSessionMapperTest {

    @Test
    fun `maps telemetry and snapshot into biometric payload`() {
        val telemetryState = TelemetryState(
            session = MovementSession(status = TelemetrySessionStatus.RUNNING),
            latestSample = TelemetrySample(
                timestampMs = 5_000L,
                totalDistanceMeters = 420.0,
                distanceDeltaMeters = 12.0,
                speedMetersPerSecond = 3.4,
                derivedAccelerationMetersPerSecondSquared = 0.6,
                effectiveAccelerationMetersPerSecondSquared = 0.5,
                movementConfidence = 0.92
            ),
            latestBiofeedbackSample = BiofeedbackSample(
                timestampMs = 5_000L,
                bpm = 148
            )
        )
        val snapshot = GameSnapshot(
            distance = 420.0,
            elapsedSeconds = 84.0,
            performanceScore = 0.75
        )

        val message = buildBiometricDataMessage(
            sessionId = "session-123",
            userId = "user-456",
            telemetryState = telemetryState,
            snapshot = snapshot,
            timestampMs = 9_999L
        )

        assertEquals(
            BiometricDataMessage(
                sessionId = "session-123",
                userId = "user-456",
                timestamp = 9_999L,
                bpm = 148,
                cadence = 90.0,
                speed = 12.24,
                pace = 4.9,
                accumulatedDistance = 0.42,
                accumulatedCalories = 63.0
            ),
            message
        )
    }

    @Test
    fun `returns null when telemetry sample is missing`() {
        val message = buildBiometricDataMessage(
            sessionId = "session-123",
            userId = "user-456",
            telemetryState = TelemetryState(),
            snapshot = GameSnapshot(),
            timestampMs = 1L
        )

        assertNull(message)
    }

    @Test
    fun `uses estimated bpm when biofeedback sample is missing`() {
        val telemetryState = TelemetryState(
            session = MovementSession(status = TelemetrySessionStatus.RUNNING),
            latestSample = TelemetrySample(
                timestampMs = 5_000L,
                totalDistanceMeters = 250.0,
                distanceDeltaMeters = 10.0,
                speedMetersPerSecond = 2.8,
                derivedAccelerationMetersPerSecondSquared = 0.3,
                effectiveAccelerationMetersPerSecondSquared = 0.2,
                movementConfidence = 0.8
            )
        )

        val message = buildBiometricDataMessage(
            sessionId = "session-123",
            userId = "user-456",
            telemetryState = telemetryState,
            snapshot = GameSnapshot(performanceScore = 0.65),
            timestampMs = 9_999L
        )

        assertEquals(137, message?.bpm)
    }
}
