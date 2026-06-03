package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme
import com.pedroaba.tccmobile.theme.AppThemeVariant
import com.pedroaba.tccmobile.theme.TccMobileTheme

@Preview
@Composable
private fun AppButtonsPreview() {
    TccMobileTheme {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            AppButton(text = "Default", onClick = {})
            AppButton(text = "Outline", variant = AppButtonVariant.Outline, onClick = {})
            AppButton(text = "Destructive", variant = AppButtonVariant.Destructive, onClick = {})
            AppButton(text = "Disabled", enabled = false, onClick = {})
        }
    }
}

@Preview
@Composable
private fun AppFormControlsPreview() {
    TccMobileTheme {
        var text by remember { mutableStateOf("Pedro") }
        var date by remember { mutableStateOf("25042026") }
        var checked by remember { mutableStateOf(true) }
        var selected by remember { mutableStateOf("crimson") }

        AppForm(
            modifier = Modifier.padding(AppTheme.spacing.lg)
        ) {
            AppFormField {
                AppFormLabel("Nome")
                AppTextInput(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = "Digite seu nome"
                )
            }

            AppFormField {
                AppFormLabel("Data")
                AppDateInput(
                    value = date,
                    onValueChange = { date = it }
                )
                AppFormError("Mensagem de erro")
            }

            AppSelect(
                options = listOf(
                    AppSelectOption("Crimson", "crimson"),
                    AppSelectOption("Military", "military"),
                    AppSelectOption("Signal", "signal")
                ),
                value = selected,
                onValueChange = { selected = it }
            )

            AppCheckbox(
                checked = checked,
                onCheckedChange = { checked = it },
                label = "Aceito os termos",
                size = AppCheckboxSize.Large
            )
        }
    }
}

@Preview
@Composable
private fun AppCardsAndBadgesPreview() {
    TccMobileTheme {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            AppCard {
                AppCardHeader {
                    AppCardTitle("Sessao de treino")
                    AppCardSubtitle("Resumo dos sinais coletados")
                }
                AppCardContent {
                    AppBody("Distancia, risco e velocidade aparecem dentro do card.")
                    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                        AppBadge("Ativo")
                        AppBadge("Alerta", variant = AppBadgeVariant.Tertiary)
                    }
                }
                AppCardFooter {
                    AppButton(text = "Abrir", onClick = {})
                    AppButton(text = "Cancelar", variant = AppButtonVariant.Outline, onClick = {})
                }
            }

            AppCallout(text = "Use callouts para informacoes contextuais curtas.")
        }
    }
}

@Preview
@Composable
private fun AppIdentityAndTypographyPreview() {
    TccMobileTheme(variant = AppThemeVariant.Military) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                AppAvatar("PB")
                AppAvatar("TC", size = AppAvatarSize.Large)
                AppSpinner(modifier = Modifier.size(28.dp))
            }

            AppDisplay("Display")
            AppHero("Hero")
            AppTitle("Title")
            AppSectionTitle("Section title")
            AppBody("Body text with secondary color and line height.")
            AppCaptionMuted("Caption muted")
            AppAccent("Accent")
        }
    }
}

@Preview
@Composable
private fun AppFullUiPreview() {
    TccMobileTheme {
        var input by remember { mutableStateOf("") }
        var selected by remember { mutableStateOf<String?>(null) }
        var checked by remember { mutableStateOf(false) }

        AppRootContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppTheme.spacing.xxl)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
            ) {
                AppHero("TCC Mobile")
                AppParagraph("Preview geral dos componentes compartilhados portados do app React Native.")

                AppCard {
                    AppCardHeader {
                        AppCardTitle("Componentes")
                        AppCardSubtitle("Botoes, campos, select, card e estados")
                    }
                    AppTextInput(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = "Campo de texto"
                    )
                    AppSelect(
                        options = listOf(
                            AppSelectOption("Primeiro", "first"),
                            AppSelectOption("Segundo", "second")
                        ),
                        value = selected,
                        onValueChange = { selected = it }
                    )
                    AppCheckbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        label = "Selecionado"
                    )
                    AppButton(text = "Continuar", onClick = {})
                }
            }
        }
    }
}
