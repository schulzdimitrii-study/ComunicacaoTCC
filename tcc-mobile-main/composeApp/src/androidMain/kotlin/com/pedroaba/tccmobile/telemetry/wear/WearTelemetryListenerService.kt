package com.pedroaba.tccmobile.telemetry.wear

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearTelemetryListenerService : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        WearTelemetryMessageParser.parse(messageEvent)?.let(WearTelemetryMessageBus::publish)
    }
}
