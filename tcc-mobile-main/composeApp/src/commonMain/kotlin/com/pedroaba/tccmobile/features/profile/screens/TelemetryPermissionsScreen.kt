package com.pedroaba.tccmobile.features.profile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import com.pedroaba.tccmobile.ui.components.AppButton
import com.pedroaba.tccmobile.ui.components.AppButtonVariant
import com.pedroaba.tccmobile.ui.components.AppCallout
import com.pedroaba.tccmobile.ui.components.AppScreenScaffold
import com.pedroaba.tccmobile.ui.components.AppSecondary
import com.pedroaba.tccmobile.ui.components.FeatureCard
import com.pedroaba.tccmobile.ui.components.ListPanel
import com.pedroaba.tccmobile.ui.components.ListRow
import com.pedroaba.tccmobile.ui.components.PanelDivider
import com.pedroaba.tccmobile.ui.components.StatusPillTone
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun TelemetryPermissionsScreen(
    telemetryState: TelemetryState = TelemetryState(),
    hasNotificationPermission: Boolean = true,
    onRequestLocationPermission: () -> Unit = {},
    onRequestNotificationPermission: () -> Unit = {},
    onOpenAppSettings: () -> Unit = {},
    onOpenLocationSettings: () -> Unit = {},
    onRefreshTelemetryAvailability: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val availability = telemetryState.availability
    val canRunForegroundTelemetry = availability.hasLocationPermission && hasNotificationPermission
    val readyForMovementTelemetry = availability.hasLocationPermission &&
        availability.isLocationEnabled &&
        availability.hasMotionSensor &&
        canRunForegroundTelemetry

    AppScreenScaffold {
        Column(verticalArrangement = Arrangement.spacedBy(com.pedroaba.tccmobile.theme.AppTheme.spacing.xs)) {
            com.pedroaba.tccmobile.ui.components.AppTitle("Permissões de telemetria")
            AppSecondary("Ative os recursos do celular usados para medir corrida, distância e manter a sessão em foreground.")
        }

        FeatureCard(
            title = if (readyForMovementTelemetry) "Telemetria pronta" else "Telemetria incompleta",
            body = "Use os switches abaixo para autorizar localização, notificações e execução em foreground. Smartwatch não entra neste fluxo.",
            status = if (readyForMovementTelemetry) "PRONTO" else "AÇÃO NECESSÁRIA",
            statusTone = if (readyForMovementTelemetry) StatusPillTone.Success else StatusPillTone.Alert
        )

        ListPanel(title = "Checklist do celular", actionLabel = if (readyForMovementTelemetry) "ok" else "pendente") {
            PermissionRow(
                title = "Localização",
                subtitle = "Permissão de GPS/localização do Android",
                enabled = availability.hasLocationPermission,
                onToggle = {
                    if (!availability.hasLocationPermission) {
                        onRequestLocationPermission()
                    } else {
                        onOpenAppSettings()
                    }
                }
            )
            PanelDivider()
            PermissionRow(
                title = "GPS ativo",
                subtitle = "Provedor de localização ligado no aparelho",
                enabled = availability.isLocationEnabled,
                onToggle = onOpenLocationSettings
            )
            PanelDivider()
            PermissionRow(
                title = "Sensores de movimento",
                subtitle = "Acelerômetro usado para cadência e movimento",
                enabled = availability.hasMotionSensor,
                onToggle = onRefreshTelemetryAvailability
            )
            PanelDivider()
            PermissionRow(
                title = "Notificações",
                subtitle = "Necessária no Android 13+ para sessão em foreground",
                enabled = hasNotificationPermission,
                onToggle = {
                    if (!hasNotificationPermission) {
                        onRequestNotificationPermission()
                    } else {
                        onOpenAppSettings()
                    }
                }
            )
            PanelDivider()
            PermissionRow(
                title = "Execução em foreground",
                subtitle = "Mantém a coleta ativa durante a corrida com serviço persistente",
                enabled = canRunForegroundTelemetry,
                onToggle = {
                    when {
                        canRunForegroundTelemetry -> onOpenAppSettings()
                        !availability.hasLocationPermission -> onRequestLocationPermission()
                        !hasNotificationPermission -> onRequestNotificationPermission()
                        else -> onRefreshTelemetryAvailability()
                    }
                }
            )
        }

        if (!availability.isLocationEnabled) {
            AppCallout(text = "O Android não permite ligar/desligar GPS nem revogar permissões diretamente pelo app. Quando um switch já está ativo, ele abre as configurações para você desativar manualmente.")
        }

        AppButton(
            text = "Voltar ao perfil",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            variant = AppButtonVariant.Outline
        )
    }
}

@Composable
private fun PermissionRow(
    title: String,
    subtitle: String,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    ListRow(
        title = title,
        subtitle = subtitle,
        trailing = {
            PermissionSwitch(
                checked = enabled,
                onClick = onToggle
            )
        }
    )
}

@Composable
private fun PermissionSwitch(
    checked: Boolean,
    onClick: () -> Unit
) {
    val trackColor = if (checked) AppTheme.colors.primary else AppTheme.colors.input
    val thumbColor = if (checked) AppTheme.colors.primaryForeground else AppTheme.colors.textSecondary
    val horizontalAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 26.dp)
            .clip(RoundedCornerShape(AppTheme.radii.full))
            .background(trackColor)
            .clickable(onClick = onClick),
        contentAlignment = horizontalAlignment
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}
