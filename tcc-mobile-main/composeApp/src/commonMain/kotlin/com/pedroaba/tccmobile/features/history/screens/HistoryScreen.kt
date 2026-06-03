package com.pedroaba.tccmobile.features.history.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.ui.components.AppBody
import com.pedroaba.tccmobile.ui.components.AppCallout
import com.pedroaba.tccmobile.ui.components.AppCaption
import com.pedroaba.tccmobile.ui.components.AppScreenScaffold
import com.pedroaba.tccmobile.ui.components.AppTitle
import com.pedroaba.tccmobile.ui.components.ListPanel
import com.pedroaba.tccmobile.ui.components.ListRow
import com.pedroaba.tccmobile.ui.components.MetricCard
import com.pedroaba.tccmobile.ui.components.MetricStrip
import com.pedroaba.tccmobile.ui.components.PanelDivider
import com.pedroaba.tccmobile.ui.components.SectionPillTabs

@Composable
fun HistoryScreen(
    onBack: () -> Unit = {},
    onWatchConnection: () -> Unit = {},
    onTabSelected: (String) -> Unit = {}
) {
    AppScreenScaffold {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            AppTitle("Histórico de hordas")
            AppCaption("Revise sessões concluídas, compare distância, duração e calorias estimadas.")
        }

        AppCallout(text = "O backend atual ainda nao publica endpoint de historico. Esta tela permanece como referencia visual ate a API de listagem ser exposta.")

        SectionPillTabs(
            options = listOf("Hoje", "Ultimo mes", "Sobreviveu"),
            selected = "Hoje"
        )

        MetricStrip {
            MetricCard(value = "37", label = "hordas registradas")
            MetricCard(value = "79%", label = "taxa de sobrevivência")
            MetricCard(value = "6.4 km", label = "melhor distância")
        }

        ListPanel(title = "Resumo do mês", actionLabel = "Período mensal") {
            AppBody("Você concluiu 9 sessões no período, com 7 hordas concluídas e média de 5,1 km por sessão.")
        }

        ListPanel(title = "Últimas sessões") {
            ListRow(title = "Distrito Industrial", subtitle = "6.4 km · 42 min · Sprint final", trailingTop = "Ontem")
            PanelDivider()
            ListRow(title = "Marginal Norte", subtitle = "4.1 km · 28 min · Offline", trailingTop = "2 dias")
            PanelDivider()
            ListRow(title = "Centro Velho", subtitle = "5.8 km · 36 min · Ritmo estavel", trailingTop = "1 semana")
        }
    }
}
