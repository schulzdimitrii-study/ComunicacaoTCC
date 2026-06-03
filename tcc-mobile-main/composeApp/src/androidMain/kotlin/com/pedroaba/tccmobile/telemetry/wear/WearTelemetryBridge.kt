package com.pedroaba.tccmobile.telemetry.wear

import com.pedroaba.tccmobile.game.telemetry.model.BiofeedbackSample
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WearTelemetryBridge {
    val isWatchConnected: StateFlow<Boolean>
    val biofeedbackSamples: Flow<BiofeedbackSample>

    fun close() = Unit
}
