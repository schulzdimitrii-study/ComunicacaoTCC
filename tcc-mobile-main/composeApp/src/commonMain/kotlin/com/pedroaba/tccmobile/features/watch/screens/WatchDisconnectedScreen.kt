package com.pedroaba.tccmobile.features.watch.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.pedroaba.tccmobile.ui.components.AppBody
import com.pedroaba.tccmobile.ui.components.AppCaption
import com.pedroaba.tccmobile.ui.components.AppScreenScaffold
import com.pedroaba.tccmobile.ui.components.AppSecondary
import com.pedroaba.tccmobile.ui.components.AppTitle
import com.pedroaba.tccmobile.ui.components.FeatureCard
import com.pedroaba.tccmobile.ui.components.ListPanel
import com.pedroaba.tccmobile.ui.components.ProgressTrack
import com.pedroaba.tccmobile.ui.components.StatusPill
import com.pedroaba.tccmobile.ui.components.StatusPillTone
import com.pedroaba.tccmobile.ui.components.TopIdentityHeader

@Composable
fun WatchDisconnectedScreen(onBack: () -> Unit = {}) {
    AppScreenScaffold {
        TopIdentityHeader(
            title = "Pedro Barbosa",
            subtitle = "Nível 12 · 37 hordas · Bio Runner",
            badge = "SURVIVOR ID"
        )

        FeatureCard(
            status = "Desconectado",
            statusTone = StatusPillTone.Alert,
            title = "Smartwatch e sensores",
            body = "Sem o smartwatch, a sessão continua disponível, mas sem BPM, cadence ou zona cardíaca em tempo real.",
            primaryAction = "Conectar relógio",
            onPrimaryAction = onBack
        )

        ListPanel(title = "Últimas hordas", actionLabel = "Ver tudo") {
            AppCaption("Distrito Industrial · Ontem")
            AppCaption("Marginal Norte · 2 dias")
        }

        ListPanel(title = "Resumo da falha") {
            AppBody("O relógio saiu da faixa de conexão durante a horda. O app manteve distância e duração, mas parou a biometria.")
            ProgressTrack(progress = 0.42f)
            StatusPill(text = "Tentar novamente", tone = StatusPillTone.Alert)
        }
    }
}
