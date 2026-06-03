package com.pedroaba.tccmobile.features.game.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.backend.online.RemoteSessionState
import com.pedroaba.tccmobile.game.models.GameSnapshot
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import kotlin.math.roundToInt

@Composable
fun SessionSignalCard(
    telemetryState: TelemetryState,
    snapshot: GameSnapshot,
    remoteSessionState: RemoteSessionState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StatusRow("Session", telemetryState.session.status.name)
            StatusRow("Online Sync", remoteSessionState.status.name)
            StatusRow("Remote Session", remoteSessionState.sessionId ?: "--")
            StatusRow("Telemetry Speed", telemetryState.latestSample?.speedMetersPerSecond?.let(::formatOneDecimal) ?: "--")
            StatusRow("Telemetry Distance", telemetryState.latestSample?.totalDistanceMeters?.roundToInt()?.toString() ?: "--")
            StatusRow("Runner Vel.", formatOneDecimal(snapshot.runnerVelocity))
            StatusRow("Horde Vel.", formatOneDecimal(snapshot.hordeVelocity))
            StatusRow("Elapsed", "${snapshot.elapsedSeconds.roundToInt()}s")
            remoteSessionState.leaderboard?.let {
                StatusRow("Your Rank", "#${it.userRank}")
            }
        }
    }
}

private fun formatOneDecimal(value: Double): String {
    return ((value * 10.0).roundToInt() / 10.0).toString()
}
