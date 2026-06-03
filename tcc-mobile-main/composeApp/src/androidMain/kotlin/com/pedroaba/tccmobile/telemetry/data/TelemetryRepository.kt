package com.pedroaba.tccmobile.telemetry.data

import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import kotlinx.coroutines.flow.StateFlow

interface TelemetryRepository {
    val telemetryState: StateFlow<TelemetryState>

    fun refreshAvailability(hasLocationPermission: Boolean)

    fun startSession()

    fun pauseSession()

    fun resumeSession()

    fun stopSession()

    fun dispose()
}
