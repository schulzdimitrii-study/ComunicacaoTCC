package com.pedroaba.tccmobile.telemetry.wear

import com.pedroaba.tccmobile.game.telemetry.model.BiofeedbackSample
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

class NoOpWearTelemetryBridge : WearTelemetryBridge {
    private val watchConnectionState = MutableStateFlow(false)

    override val isWatchConnected: StateFlow<Boolean> = watchConnectionState
    override val biofeedbackSamples: Flow<BiofeedbackSample> = emptyFlow()
}
