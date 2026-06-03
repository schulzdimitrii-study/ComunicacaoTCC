package com.pedroaba.tccmobile.telemetry.wear

import android.content.Context
import com.google.android.gms.wearable.Wearable
import com.pedroaba.tccmobile.game.models.GameSnapshot
import com.pedroaba.tccmobile.game.models.SessionConfig
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import org.json.JSONObject

class WatchGameProgressBridge(context: Context) {
    private val appContext = context.applicationContext
    private val nodeClient = Wearable.getNodeClient(appContext)
    private val messageClient = Wearable.getMessageClient(appContext)

    fun publish(telemetryState: TelemetryState, snapshot: GameSnapshot, sessionConfig: SessionConfig) {
        val payload = buildPayload(telemetryState, snapshot, sessionConfig).toByteArray(Charsets.UTF_8)
        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, MESSAGE_PATH, payload)
            }
        }
    }

    private fun buildPayload(
        telemetryState: TelemetryState,
        snapshot: GameSnapshot,
        sessionConfig: SessionConfig
    ): String {
        val distanceMeters = telemetryState.latestSample?.totalDistanceMeters ?: snapshot.distance
        val progress = if (sessionConfig.goalDistance > 0.0) {
            (distanceMeters / sessionConfig.goalDistance).coerceIn(0.0, 1.0)
        } else {
            0.0
        }

        return JSONObject()
            .put("tipo", "game_progress")
            .put("status", telemetryState.session.status.name)
            .put("result", snapshot.result)
            .put("distanceMeters", distanceMeters)
            .put("goalDistanceMeters", sessionConfig.goalDistance)
            .put("progress", progress)
            .put("bpm", telemetryState.latestBiofeedbackSample?.bpm ?: JSONObject.NULL)
            .put("risk", snapshot.risk.coerceIn(0.0, 1.0))
            .put("hordePressure", snapshot.hordePressure.coerceIn(0.0, 1.0))
            .put("performanceScore", snapshot.performanceScore.coerceIn(0.0, 1.0))
            .put("runnerVelocity", snapshot.runnerVelocity)
            .put("hordeVelocity", snapshot.hordeVelocity)
            .put("elapsedSeconds", snapshot.elapsedSeconds)
            .put("timestamp", System.currentTimeMillis())
            .toString()
    }

    private companion object {
        const val MESSAGE_PATH = "/game-progress"
    }
}
