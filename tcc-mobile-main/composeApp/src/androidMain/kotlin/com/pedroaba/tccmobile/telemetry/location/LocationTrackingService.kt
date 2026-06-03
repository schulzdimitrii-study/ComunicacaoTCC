package com.pedroaba.tccmobile.telemetry.location

import com.pedroaba.tccmobile.game.telemetry.model.LocationPoint
import kotlinx.coroutines.flow.Flow

data class LocationTrackingConfig(
    val updateIntervalMs: Long = 2_000L,
    val minUpdateIntervalMs: Long = 1_000L,
    val minUpdateDistanceMeters: Float = 2f
)

interface LocationTrackingService {
    fun locationUpdates(config: LocationTrackingConfig = LocationTrackingConfig()): Flow<LocationPoint>

    fun isLocationEnabled(): Boolean
}
