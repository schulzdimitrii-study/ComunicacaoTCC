package com.example.datalayertest

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import androidx.core.content.ContextCompat
import org.json.JSONObject

class PhoneGameProgressListenerService : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            GameProgressStore.MESSAGE_PATH -> GameProgressStore.saveFromMessage(this, messageEvent)
            CONTROL_MESSAGE_PATH -> handleControlMessage(messageEvent)
        }
    }

    private fun handleControlMessage(messageEvent: MessageEvent) {
        val payload = runCatching { String(messageEvent.data, Charsets.UTF_8) }.getOrNull() ?: return
        val json = runCatching { JSONObject(payload) }.getOrNull() ?: return

        when (json.optString("command")) {
            CONTROL_COMMAND_START -> {
                ContextCompat.startForegroundService(
                    this,
                    TelemetrySessionService.createStartIntent(this)
                )
            }
            CONTROL_COMMAND_STOP -> {
                GameProgressStore.clear(this)
                startService(TelemetrySessionService.createStopIntent(this))
            }
        }
    }

    private companion object {
        const val CONTROL_MESSAGE_PATH = "/telemetry-control"
        const val CONTROL_COMMAND_START = "start"
        const val CONTROL_COMMAND_STOP = "stop"
    }
}
