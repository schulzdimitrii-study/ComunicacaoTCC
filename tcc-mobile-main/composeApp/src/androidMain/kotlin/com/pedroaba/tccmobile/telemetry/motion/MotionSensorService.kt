package com.pedroaba.tccmobile.telemetry.motion

import com.pedroaba.tccmobile.game.telemetry.model.AccelerationSample
import kotlinx.coroutines.flow.Flow

data class MotionTrackingConfig(
    val samplingPeriodUs: Int = 100_000
)

interface MotionSensorService {
    fun accelerationUpdates(config: MotionTrackingConfig = MotionTrackingConfig()): Flow<AccelerationSample>

    fun isSensorAvailable(): Boolean
}
