package com.pedroaba.tccmobile.game.telemetry.usecase

import com.pedroaba.tccmobile.game.telemetry.model.AccelerationSample
import com.pedroaba.tccmobile.game.telemetry.model.LocationPoint
import com.pedroaba.tccmobile.game.telemetry.model.MotionSensorType
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryProcessorConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MovementTelemetryProcessorTest {
    @Test
    fun accumulatesDistanceAndDerivedSpeedFromAcceptedLocations() {
        val processor = MovementTelemetryProcessor(
            config = TelemetryProcessorConfig(
                minDistanceDeltaMeters = 1.0,
                maxAcceptedAccuracyMeters = 20.0,
                maxAcceptedSpeedMetersPerSecond = 12.0,
                speedSmoothingFactor = 1.0,
                accelerationSmoothingFactor = 1.0
            )
        )

        val first = processor.onLocation(
            LocationPoint(
                timestampMs = 1_000L,
                latitude = -23.550520,
                longitude = -46.633308,
                accuracyMeters = 5.0
            )
        )
        val second = processor.onLocation(
            LocationPoint(
                timestampMs = 3_000L,
                latitude = -23.550520,
                longitude = -46.633108,
                accuracyMeters = 5.0
            )
        )

        assertNotNull(first)
        assertNotNull(second)
        assertTrue(second.totalDistanceMeters > 15.0)
        assertTrue(second.speedMetersPerSecond > 7.0)
        assertTrue(second.derivedAccelerationMetersPerSecondSquared >= 0.0)
        assertTrue(second.isMoving)
    }

    @Test
    fun ignoresInaccurateLocationSamples() {
        val processor = MovementTelemetryProcessor(
            config = TelemetryProcessorConfig(maxAcceptedAccuracyMeters = 15.0)
        )

        val ignored = processor.onLocation(
            LocationPoint(
                timestampMs = 1_000L,
                latitude = -23.550520,
                longitude = -46.633308,
                accuracyMeters = 42.0
            )
        )

        assertNull(ignored)
    }

    @Test
    fun mergesRawAccelerationIntoCurrentTelemetrySnapshot() {
        val processor = MovementTelemetryProcessor(
            config = TelemetryProcessorConfig(rawAccelerationSmoothingFactor = 1.0)
        )

        processor.onLocation(
            LocationPoint(
                timestampMs = 1_000L,
                latitude = -23.550520,
                longitude = -46.633308,
                accuracyMeters = 5.0
            )
        )

        val sample = processor.onAcceleration(
            AccelerationSample(
                timestampMs = 1_200L,
                magnitudeMetersPerSecondSquared = 1.8,
                sensorType = MotionSensorType.LINEAR_ACCELERATION
            )
        )

        assertNotNull(sample)
        val rawAcceleration = sample.rawAccelerationMetersPerSecondSquared
        assertNotNull(rawAcceleration)
        assertEquals(1.8, rawAcceleration, 0.0001)
        assertEquals(1.8, sample.effectiveAccelerationMetersPerSecondSquared, 0.0001)
    }

    @Test
    fun ignoresImplausibleLocationJump() {
        val processor = MovementTelemetryProcessor(
            config = TelemetryProcessorConfig(
                minDistanceDeltaMeters = 1.0,
                maxAcceptedAccuracyMeters = 20.0,
                maxAcceptedSpeedMetersPerSecond = 8.0,
                maxAcceptedDistanceDeltaMeters = 40.0
            )
        )

        processor.onLocation(
            LocationPoint(
                timestampMs = 1_000L,
                latitude = -23.550520,
                longitude = -46.633308,
                accuracyMeters = 5.0
            )
        )

        val jump = processor.onLocation(
            LocationPoint(
                timestampMs = 2_000L,
                latitude = -23.540520,
                longitude = -46.623308,
                accuracyMeters = 5.0
            )
        )

        assertEquals(null, jump)
    }

    @Test
    fun emitsStaleStationarySnapshotWhenTelemetryStopsUpdating() {
        val processor = MovementTelemetryProcessor(
            config = TelemetryProcessorConfig(
                staleLocationThresholdMs = 3_000L,
                speedSmoothingFactor = 1.0,
                accelerationSmoothingFactor = 1.0
            )
        )

        processor.onLocation(
            LocationPoint(
                timestampMs = 1_000L,
                latitude = -23.550520,
                longitude = -46.633308,
                accuracyMeters = 5.0
            )
        )
        processor.onLocation(
            LocationPoint(
                timestampMs = 2_000L,
                latitude = -23.550520,
                longitude = -46.633208,
                accuracyMeters = 5.0
            )
        )

        val staleSnapshot = processor.snapshotAt(7_000L)

        assertNotNull(staleSnapshot)
        assertTrue(staleSnapshot.isLocationStale)
        assertEquals(false, staleSnapshot.isMoving)
        assertEquals(0.0, staleSnapshot.speedMetersPerSecond, 0.0001)
    }
}
