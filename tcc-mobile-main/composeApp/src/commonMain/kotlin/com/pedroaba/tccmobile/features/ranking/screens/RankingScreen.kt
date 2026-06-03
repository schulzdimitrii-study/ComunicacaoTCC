package com.pedroaba.tccmobile.features.ranking.screens

import com.pedroaba.tccmobile.backend.online.RemoteSessionState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.ui.components.AppBody
import com.pedroaba.tccmobile.ui.components.AppCaption
import com.pedroaba.tccmobile.ui.components.AppMetric
import com.pedroaba.tccmobile.ui.components.AppOverline
import com.pedroaba.tccmobile.ui.components.AppScreenScaffold
import com.pedroaba.tccmobile.ui.components.AppSecondary
import com.pedroaba.tccmobile.ui.components.AppSecondarySemiBold
import com.pedroaba.tccmobile.ui.components.AppTitle
import com.pedroaba.tccmobile.ui.components.ListPanel
import com.pedroaba.tccmobile.ui.components.ListRow
import com.pedroaba.tccmobile.ui.components.MetricCard
import com.pedroaba.tccmobile.ui.components.PanelDivider
import com.pedroaba.tccmobile.ui.components.StatusPill
import com.pedroaba.tccmobile.ui.components.StatusPillTone
import com.pedroaba.tccmobile.ui.components.SurvivorAvatar
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun RankingScreen(
    remoteSessionState: RemoteSessionState = RemoteSessionState(),
    currentUserId: String = "",
    currentUserName: String = "Você",
    onTabSelected: (String) -> Unit = {}
) {
    AppScreenScaffold {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            AppTitle("Ranking da infestação")
            AppCaption("Veja sua posição na sessão online atual quando o backend estiver transmitindo o leaderboard.")
        }

        val leaderboard = remoteSessionState.leaderboard
        if (leaderboard == null) {
            ListPanel(title = "Leaderboard indisponível", actionLabel = remoteSessionState.status.name) {
                AppBody("Inicie uma sessão de corrida para receber sua posição em tempo real. O backend atual ainda não publica ranking global fora da sessão ativa.")
            }
        } else {
            ListPanel(title = "Seu status", actionLabel = "sessão ativa") {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        AppTitle("#${leaderboard.userRank} na corrida")
                        AppCaption("Sessão ${leaderboard.sessionId.take(8)}")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            value = "${leaderboard.entries.size}",
                            label = "corredores no topo"
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            value = leaderboard.hordeVirtualDistanceKm?.let(::formatKm) ?: "--",
                            label = "distância da horda"
                        )
                    }
                }
            }

            if (leaderboard.entries.isNotEmpty()) {
                ListPanel(title = "Top 3 da sessão", actionLabel = "tempo real") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        leaderboard.entries.take(3).forEachIndexed { index, entry ->
                            PodiumCard(
                                position = "#${entry.rank}",
                                name = if (entry.userId == currentUserId) currentUserName else "Atleta ${index + 1}",
                                score = formatKm(entry.distanceKm),
                                active = index == 0
                            )
                        }
                    }
                }
            }

            ListPanel(title = "Leaderboard da sessão", actionLabel = remoteSessionState.status.name) {
                leaderboard.entries.forEachIndexed { index, entry ->
                    val isCurrentUser = entry.userId == currentUserId
                    LeaderboardRow(
                        rank = entry.rank.toString(),
                        name = if (isCurrentUser) currentUserName else entry.userId.take(8),
                        score = formatKm(entry.distanceKm),
                        highlight = isCurrentUser
                    )
                    if (index != leaderboard.entries.lastIndex) {
                        PanelDivider()
                    }
                }
                if (leaderboard.entries.isEmpty()) {
                    AppBody("A sessão ainda não tem distâncias registradas no leaderboard.")
                }
            }
        }
    }
}

@Composable
private fun PodiumCard(
    position: String,
    name: String,
    score: String,
    active: Boolean = false
) {
    Surface(
        modifier = Modifier.width(92.dp),
        shape = RoundedCornerShape(18.dp),
        color = AppTheme.colors.card,
        contentColor = AppTheme.colors.textPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppOverline(score)
                AppMetric(position)
                AppCaption(name)
            }
            if (active) {
                StatusPill(text = "líder", tone = StatusPillTone.Alert)
            }
        }
    }
}

@Composable
private fun LeaderboardRow(
    rank: String,
    name: String,
    score: String,
    highlight: Boolean = false
) {
    ListRow(
        title = name,
        subtitle = if (highlight) "você" else "score semanal",
        trailingTop = score,
        leading = { SurvivorAvatar(initials = rank) },
        trailing = {
            if (highlight) {
                StatusPill(text = "#$rank", tone = StatusPillTone.Alert)
            } else {
                AppSecondarySemiBold("#$rank")
            }
        }
    )
}

private fun formatKm(value: Double): String {
    val rounded = kotlin.math.round(value * 100.0) / 100.0
    return "$rounded km"
}
