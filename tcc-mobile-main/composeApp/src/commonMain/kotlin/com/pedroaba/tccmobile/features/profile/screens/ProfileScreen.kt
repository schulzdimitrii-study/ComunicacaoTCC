package com.pedroaba.tccmobile.features.profile.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pedroaba.tccmobile.backend.online.RemoteSessionState
import com.pedroaba.tccmobile.backend.online.RemoteSessionStatus
import com.pedroaba.tccmobile.ui.components.AppCallout
import com.pedroaba.tccmobile.ui.components.AppCaption
import com.pedroaba.tccmobile.ui.components.AppScreenScaffold
import com.pedroaba.tccmobile.ui.components.AppSecondary
import com.pedroaba.tccmobile.ui.components.FeatureCard
import com.pedroaba.tccmobile.ui.components.ListPanel
import com.pedroaba.tccmobile.ui.components.ListRow
import com.pedroaba.tccmobile.ui.components.MetricCard
import com.pedroaba.tccmobile.ui.components.MetricStrip
import com.pedroaba.tccmobile.ui.components.PanelDivider
import com.pedroaba.tccmobile.ui.components.StatusPill
import com.pedroaba.tccmobile.ui.components.StatusPillTone
import com.pedroaba.tccmobile.ui.components.TopIdentityHeader

@Composable
fun ProfileScreen(
    userName: String = "Você",
    userEmail: String = "",
    currentUserId: String = "",
    remoteSessionState: RemoteSessionState = RemoteSessionState(),
    onEditProfile: () -> Unit = {},
    onTabSelected: (String) -> Unit = {}
) {
    val leaderboard = remoteSessionState.leaderboard
    val currentUserEntry = leaderboard?.entries?.firstOrNull { it.userId == currentUserId }
    val isSessionActive = remoteSessionState.status == RemoteSessionStatus.ACTIVE ||
        remoteSessionState.status == RemoteSessionStatus.CONNECTING ||
        remoteSessionState.status == RemoteSessionStatus.STARTING

    AppScreenScaffold {
        TopIdentityHeader(
            title = userName,
            subtitle = userEmail.ifBlank { "Email não informado" }
        )

        AppCallout(text = "Identidade sincronizada pela autenticação. Estatísticas históricas permanecem vazias até existirem endpoints dedicados no backend.")

        MetricStrip {
            MetricCard(
                modifier = Modifier.weight(1f),
                value = currentUserEntry?.distanceKm?.let(::formatKm) ?: "--",
                label = "distância na sessão"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                value = leaderboard?.userRank?.let { "#$it" } ?: "--",
                label = "rank atual"
            )
        }

        FeatureCard(
            title = "Telemetria do celular",
            body = "Gerencie GPS, sensores de movimento e serviço em foreground. Smartwatch fica fora deste fluxo por enquanto.",
            status = if (isSessionActive) "Sessão ativa" else "Sem sessão ativa",
            statusTone = if (isSessionActive) StatusPillTone.Success else StatusPillTone.Neutral,
            primaryAction = "Permissões",
            onPrimaryAction = { onTabSelected("telemetry_permissions") },
            secondaryAction = "Editar perfil",
            onSecondaryAction = onEditProfile,
            footer = {
                PanelDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        AppCaption("GPS e movimento")
                        AppSecondary("permissões do celular")
                    }
                    Column {
                        AppCaption("Backend")
                        AppSecondary(remoteSessionState.status.name)
                    }
                }
            }
        )

        ListPanel(title = "Sessão atual", actionLabel = if (leaderboard == null) "sem dados" else "tempo real") {
            if (leaderboard == null) {
                AppSecondary("Inicie uma horda para receber leaderboard e distância em tempo real.")
            } else {
                leaderboard.entries.take(3).forEachIndexed { index, entry ->
                    ListRow(
                        title = if (entry.userId == currentUserId) userName else entry.userId.take(8),
                        subtitle = "#${entry.rank}",
                        trailingTop = formatKm(entry.distanceKm)
                    )
                    if (index != leaderboard.entries.take(3).lastIndex) {
                        PanelDivider()
                    }
                }
            }
        }
    }
}

private fun formatKm(value: Double): String = "${kotlin.math.round(value * 100.0) / 100.0} km"
