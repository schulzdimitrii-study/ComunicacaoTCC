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
import com.pedroaba.tccmobile.game.models.GameSnapshot
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun StatusDisplay(snapshot: GameSnapshot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StatusRow("Distance", "${snapshot.distance.toInt()}m")
            StatusRow("Score", "${(snapshot.performanceScore * 100).toInt()}%")
            StatusRow("Risk", "${(snapshot.risk * 100).toInt()}%")
            StatusRow("Horde Pressure", "${(snapshot.hordePressure * 100).toInt()}%")
            StatusRow("Result", snapshot.result.uppercase(), 
                color = when(snapshot.result) {
                    "escaped" -> AppTheme.colors.glow
                    "caught" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}