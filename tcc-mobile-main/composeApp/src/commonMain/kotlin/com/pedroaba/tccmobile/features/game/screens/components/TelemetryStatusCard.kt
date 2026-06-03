package com.pedroaba.tccmobile.features.game.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.backend.online.RemoteSessionState
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import kotlin.math.roundToInt

@Composable
fun TelemetryStatusCard(
    telemetryState: TelemetryState,
    lastEscapeMetricsLabel: String,
    remoteSessionState: RemoteSessionState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "MOVEMENT METRICS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val sample = telemetryState.latestSample
            val metrics = telemetryState.latestEscapeMetrics

            StatusRow("Distance", sample?.totalDistanceMeters?.roundToInt()?.let { "${it}m" } ?: "--")
            StatusRow("Speed", sample?.speedMetersPerSecond?.let(::formatOneDecimal) ?: "--")
            StatusRow("Acceleration", sample?.effectiveAccelerationMetersPerSecondSquared?.let(::formatOneDecimal) ?: "--")
            StatusRow("Confidence", sample?.movementConfidence?.let { "${(it * 100).roundToInt()}%" } ?: "--")
            StatusRow("Escape Score", lastEscapeMetricsLabel)
            StatusRow("Normalized Speed", metrics?.normalizedSpeed?.let { "${(it * 100).roundToInt()}%" } ?: "--")
            StatusRow("Normalized Distance", metrics?.normalizedDistance?.let { "${(it * 100).roundToInt()}%" } ?: "--")
            StatusRow(
                "Horde Distance",
                remoteSessionState.leaderboard?.hordeVirtualDistanceKm?.let { "${formatOneDecimal(it)} km" } ?: "--"
            )

            val issuesLabel = telemetryState.availability.issues
                .filterNot { it.name == "WATCH_UNAVAILABLE" }
                .joinToString()
                .ifEmpty { "none" }

            Spacer(modifier = Modifier.height(8.dp))
            StatusRow("Issues", issuesLabel)
        }
    }
}

private fun formatOneDecimal(value: Double): String {
    return ((value * 10.0).roundToInt() / 10.0).toString()
}
