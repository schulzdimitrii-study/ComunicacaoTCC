package com.pedroaba.tccmobile.telemetry.wear

import com.pedroaba.tccmobile.game.telemetry.model.BiofeedbackSample
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

object WearTelemetryMessageBus {
    private val watchConnectionState = MutableStateFlow(false)
    private val sampleEvents = MutableSharedFlow<BiofeedbackSample>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val isWatchConnected: StateFlow<Boolean> = watchConnectionState.asStateFlow()
    val biofeedbackSamples: SharedFlow<BiofeedbackSample> = sampleEvents.asSharedFlow()

    fun publish(sample: BiofeedbackSample) {
        watchConnectionState.value = true
        sampleEvents.tryEmit(sample)
    }

    fun updateConnectionState(isConnected: Boolean) {
        watchConnectionState.value = isConnected
    }
}
