package com.example.datalayertest

import android.content.Context
import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import org.json.JSONObject
import kotlin.math.roundToInt

data class GameProgressState(
    val status: String = "IDLE",
    val result: String = "running",
    val distanceMeters: Double = 0.0,
    val goalDistanceMeters: Double = 1000.0,
    val progress: Double = 0.0,
    val bpm: Int? = null,
    val risk: Double = 0.0,
    val hordePressure: Double = 0.0,
    val performanceScore: Double = 0.0,
    val runnerVelocity: Double = 0.0,
    val hordeVelocity: Double = 0.0,
    val elapsedSeconds: Double = 0.0,
    val updatedAtMs: Long = 0L
) {
    val isRunning: Boolean
        get() = status == "RUNNING" || status == "PAUSED"

    val progressPercent: Int
        get() = (progress.coerceIn(0.0, 1.0) * 100).roundToInt()
}

object GameProgressStore {
    const val ACTION_GAME_PROGRESS_CHANGED = "com.example.datalayertest.action.GAME_PROGRESS_CHANGED"
    const val MESSAGE_PATH = "/game-progress"

    private const val PREFS_NAME = "game_progress_prefs"
    private const val KEY_PAYLOAD = "payload"

    fun saveFromMessage(context: Context, event: MessageEvent) {
        if (event.path != MESSAGE_PATH) return
        val payload = runCatching { String(event.data, Charsets.UTF_8) }.getOrNull() ?: return
        if (runCatching { JSONObject(payload) }.isFailure) return

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PAYLOAD, payload)
            .apply()

        context.sendBroadcast(
            Intent(ACTION_GAME_PROGRESS_CHANGED).apply {
                setPackage(context.packageName)
            }
        )
    }

    fun load(context: Context): GameProgressState {
        val payload = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PAYLOAD, null)
            ?: return GameProgressState()
        val json = runCatching { JSONObject(payload) }.getOrNull() ?: return GameProgressState()

        return GameProgressState(
            status = json.optionalString("status") ?: "IDLE",
            result = json.optionalString("result") ?: "running",
            distanceMeters = json.optionalDouble("distanceMeters") ?: 0.0,
            goalDistanceMeters = json.optionalDouble("goalDistanceMeters") ?: 1000.0,
            progress = json.optionalDouble("progress") ?: 0.0,
            bpm = json.optionalInt("bpm"),
            risk = json.optionalDouble("risk") ?: 0.0,
            hordePressure = json.optionalDouble("hordePressure") ?: 0.0,
            performanceScore = json.optionalDouble("performanceScore") ?: 0.0,
            runnerVelocity = json.optionalDouble("runnerVelocity") ?: 0.0,
            hordeVelocity = json.optionalDouble("hordeVelocity") ?: 0.0,
            elapsedSeconds = json.optionalDouble("elapsedSeconds") ?: 0.0,
            updatedAtMs = json.optionalLong("timestamp") ?: 0L
        )
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_PAYLOAD)
            .apply()

        context.sendBroadcast(
            Intent(ACTION_GAME_PROGRESS_CHANGED).apply {
                setPackage(context.packageName)
            }
        )
    }

    private fun JSONObject.optionalString(name: String): String? {
        if (!has(name) || isNull(name)) return null
        return runCatching { getString(name) }.getOrNull()
    }

    private fun JSONObject.optionalInt(name: String): Int? {
        if (!has(name) || isNull(name)) return null
        return runCatching { getInt(name) }.getOrNull()
    }

    private fun JSONObject.optionalDouble(name: String): Double? {
        if (!has(name) || isNull(name)) return null
        return runCatching { getDouble(name) }.getOrNull()
    }

    private fun JSONObject.optionalLong(name: String): Long? {
        if (!has(name) || isNull(name)) return null
        return runCatching { getLong(name) }.getOrNull()
    }
}
