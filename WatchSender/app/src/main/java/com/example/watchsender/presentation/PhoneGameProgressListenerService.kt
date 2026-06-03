package com.example.datalayertest

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class PhoneGameProgressListenerService : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        GameProgressStore.saveFromMessage(this, messageEvent)
    }
}
