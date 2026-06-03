package com.pedroaba.tccmobile.telemetry.wear

import android.content.Context
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.pedroaba.tccmobile.game.telemetry.model.BiofeedbackSample
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class AndroidWearTelemetryBridge(context: Context) : WearTelemetryBridge {
    private val appContext = context.applicationContext
    private val messageClient = Wearable.getMessageClient(appContext)
    private val nodeClient = Wearable.getNodeClient(appContext)

    private val messageListener = MessageClient.OnMessageReceivedListener { event ->
        WearTelemetryMessageParser.parse(event)?.let(WearTelemetryMessageBus::publish)
    }

    override val isWatchConnected: StateFlow<Boolean> = WearTelemetryMessageBus.isWatchConnected
    override val biofeedbackSamples: Flow<BiofeedbackSample> = WearTelemetryMessageBus.biofeedbackSamples

    init {
        messageClient.addListener(messageListener)
        refreshConnectedNodes()
    }

    override fun close() {
        messageClient.removeListener(messageListener)
    }

    private fun refreshConnectedNodes() {
        nodeClient.connectedNodes
            .addOnSuccessListener { nodes ->
                WearTelemetryMessageBus.updateConnectionState(nodes.isNotEmpty())
            }
            .addOnFailureListener {
                WearTelemetryMessageBus.updateConnectionState(false)
            }
    }
}
