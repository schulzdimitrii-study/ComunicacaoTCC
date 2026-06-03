package com.pedroaba.tccmobile.features.watch.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.ui.components.AppBody
import com.pedroaba.tccmobile.ui.components.AppCaption
import com.pedroaba.tccmobile.ui.components.AppScreenScaffold
import com.pedroaba.tccmobile.ui.components.AppSecondary
import com.pedroaba.tccmobile.ui.components.AppTitle
import com.pedroaba.tccmobile.ui.components.IconBadge
import com.pedroaba.tccmobile.ui.components.ListPanel
import com.pedroaba.tccmobile.ui.components.ListRow
import com.pedroaba.tccmobile.ui.components.PanelDivider
import com.pedroaba.tccmobile.ui.components.ProgressTrack
import com.pedroaba.tccmobile.ui.components.StatusPill
import com.pedroaba.tccmobile.ui.components.StatusPillTone

@Composable
fun WatchConnectionStatesScreen(
    onBack: () -> Unit = {},
    onWatchDisconnected: () -> Unit = {}
) {
    AppScreenScaffold {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            AppTitle("Estados da conexão do smartwatch")
            AppCaption("Fluxo simples para parear, sincronizar biometria e tratar falhas de última milha da sessão.")
        }

        ListPanel(title = "Pronto para conectar") {
            AppBody("Aguardando pareamento do dispositivo. Toque para ativar Bluetooth, zona cardíaca e GPS.")
            StatusPill(text = "Conectar smartwatch", tone = StatusPillTone.Alert)
        }

        ListPanel(title = "Sincronizando dados biométricos") {
            AppBody("Validando BPM, cadence, pace e distância consumida da sessão.")
            ProgressTrack(progress = 0.68f)
            AppCaption("Sincronizando sinais vitais · 68%")
        }

        ListPanel(title = "Biométricos ativos para a sessão") {
            WatchSignalRow(icon = Icons.Filled.Bluetooth, title = "Bluetooth", subtitle = "link ativo ha 3m")
            PanelDivider()
            WatchSignalRow(icon = Icons.Filled.Favorite, title = "Telemetria ao vivo", subtitle = "BPM liberado em 12s")
            PanelDivider()
            WatchSignalRow(icon = Icons.Filled.LocationOn, title = "Pareado com sucesso", subtitle = "dispositivo e horda sincronizados")
        }

        ListPanel(title = "Falha na conexão") {
            AppSecondary("A conexão caiu antes de concluir a leitura dos sensores. Tente novamente ou siga em modo degradado.")
            StatusPill(text = "AUTENTICAÇÃO FALHOU", tone = StatusPillTone.Alert)
        }
    }
}

@Composable
private fun WatchSignalRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    ListRow(
        title = title,
        subtitle = subtitle,
        leading = { IconBadge(icon = icon) },
        trailing = { StatusPill(text = "ativo", tone = StatusPillTone.Success) }
    )
}
