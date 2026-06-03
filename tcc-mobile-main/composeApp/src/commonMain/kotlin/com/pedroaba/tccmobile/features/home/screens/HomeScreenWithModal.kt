package com.pedroaba.tccmobile.features.home.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pedroaba.tccmobile.backend.online.HordeCatalogStatus
import com.pedroaba.tccmobile.backend.online.RemoteSessionState
import com.pedroaba.tccmobile.backend.online.RemoteSessionStatus
import com.pedroaba.tccmobile.ui.components.AppScreenScaffold
import com.pedroaba.tccmobile.ui.components.AppSecondary
import com.pedroaba.tccmobile.ui.components.FeatureCard
import com.pedroaba.tccmobile.ui.components.MetricCard
import com.pedroaba.tccmobile.ui.components.MetricStrip
import com.pedroaba.tccmobile.ui.components.PanelDivider
import com.pedroaba.tccmobile.ui.components.StatusPill
import com.pedroaba.tccmobile.ui.components.StatusPillTone
import com.pedroaba.tccmobile.ui.components.TopIdentityHeader
import com.pedroaba.tccmobile.ui.components.ListPanel
import com.pedroaba.tccmobile.ui.components.ListRow
import kotlin.math.round

@Composable
fun HomeScreenWithModal(
    userName: String = "Você",
    currentUserId: String = "",
    remoteSessionState: RemoteSessionState = RemoteSessionState(),
    onDismissModal: () -> Unit = {},
    onTabSelected: (String) -> Unit = {}
) {
    val leaderboard = remoteSessionState.leaderboard
    val currentUserEntry = leaderboard?.entries?.firstOrNull { it.userId == currentUserId }
    val isSessionActive = remoteSessionState.status == RemoteSessionStatus.ACTIVE ||
        remoteSessionState.status == RemoteSessionStatus.CONNECTING ||
        remoteSessionState.status == RemoteSessionStatus.STARTING

    AppScreenScaffold {
        TopIdentityHeader(
            title = "Boa noite, $userName",
            avatarName = userName,
            subtitle = remoteSessionState.sessionId?.let { "Sessão ${it.take(8)} · ${remoteSessionState.status.name.lowercase()}" }
                ?: "Conta conectada · aguardando sessão"
        )

        MetricStrip {
            MetricCard(
                modifier = Modifier.weight(1f),
                value = remoteSessionState.hordes.size.toString(),
                label = "hordas carregadas"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                value = leaderboard?.userRank?.let { "#$it" } ?: "--",
                label = "rank na sessão"
            )
        }

        FeatureCard(
            status = if (isSessionActive) "SESSÃO ATIVA" else "WEARABLE NÃO INTEGRADO",
            statusTone = if (isSessionActive) StatusPillTone.Success else StatusPillTone.Neutral,
            title = "Telemetria disponível",
            body = "O app envia localização, velocidade, cadência estimada e distância ao backend. BPM depende da ponte de wearable nativa.",
            primaryAction = "Voltar",
            onPrimaryAction = onDismissModal,
            secondaryAction = if (isSessionActive) "Ver sessão" else "Entendi",
            onSecondaryAction = onDismissModal,
            footer = {
                PanelDivider()
                Column(verticalArrangement = Arrangement.spacedBy(com.pedroaba.tccmobile.theme.AppTheme.spacing.xs)) {
                    StatusPill(
                        text = when (remoteSessionState.hordeCatalogStatus) {
                            HordeCatalogStatus.LOADED -> "Hordas sincronizadas"
                            HordeCatalogStatus.LOADING -> "Sincronizando hordas"
                            HordeCatalogStatus.ERROR -> "Falha no catálogo"
                            HordeCatalogStatus.IDLE -> "Catálogo pendente"
                        },
                        tone = if (remoteSessionState.hordeCatalogStatus == HordeCatalogStatus.ERROR) {
                            StatusPillTone.Alert
                        } else {
                            StatusPillTone.Neutral
                        }
                    )
                    AppSecondary(
                        currentUserEntry?.distanceKm?.let { "Sua distância enviada: ${formatKm(it)}." }
                            ?: "Ainda não há amostra de leaderboard para esta sessão."
                    )
                }
            }
        )

        ListPanel(title = "Leaderboard da sessão", actionLabel = if (leaderboard == null) "sem dados" else "tempo real") {
            if (leaderboard == null || leaderboard.entries.isEmpty()) {
                AppSecondary("O backend ainda não transmitiu posições para a sessão atual.")
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

private fun formatKm(value: Double): String {
    val rounded = round(value * 100.0) / 100.0
    return "$rounded km"
}
