package com.pedroaba.tccmobile.telemetry.wear

import com.google.android.gms.wearable.MessageEvent
import com.pedroaba.tccmobile.game.telemetry.model.BiofeedbackSample
import com.pedroaba.tccmobile.game.telemetry.model.BiofeedbackSource
import org.json.JSONObject

object WearTelemetryMessageParser {
    const val MESSAGE_PATH = "/telemetry"

    fun parse(event: MessageEvent): BiofeedbackSample? {
        if (event.path != MESSAGE_PATH) return null
        val payload = runCatching { String(event.data, Charsets.UTF_8) }.getOrNull() ?: return null
        val json = runCatching { JSONObject(payload) }.getOrNull() ?: return null
        val bpm = json.optionalInt("batimentoBpm")
            ?: json.optionalInt("bpm")
            ?: json.optionalDoubleAsInt("heartRateBpm")
            ?: return null

        return BiofeedbackSample(
            timestampMs = json.optionalLong("timestamp") ?: System.currentTimeMillis(),
            bpm = bpm,
            source = BiofeedbackSource.WATCH
        )
    }

    private fun JSONObject.optionalInt(name: String): Int? {
        if (!has(name) || isNull(name)) return null
        return runCatching { getInt(name) }.getOrNull()
    }

    private fun JSONObject.optionalDoubleAsInt(name: String): Int? {
        if (!has(name) || isNull(name)) return null
        return runCatching { getDouble(name).toInt() }.getOrNull()
    }

    private fun JSONObject.optionalLong(name: String): Long? {
        if (!has(name) || isNull(name)) return null
        return runCatching { getLong(name) }.getOrNull()
    }
}
