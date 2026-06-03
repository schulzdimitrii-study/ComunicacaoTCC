package com.pedroaba.tccmobile.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.pedroaba.tccmobile.theme.TccMobileTheme
import com.pedroaba.tccmobile.ui.components.navigation.FloatingTabBar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Rule

class AppComponentsInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun appAvatar_rendersUppercaseInitialsAndTruncatesFallback() {
        composeRule.setComponentContent {
            AppAvatar(fallback = "pedro")
            AppAvatar(fallback = "z", size = AppAvatarSize.Small)
        }

        composeRule.onNodeWithText("PE").assertIsDisplayed()
        composeRule.onNodeWithText("Z").assertIsDisplayed()
    }

    @Test
    fun appBadge_rendersAllPublicVariantsAndCustomColors() {
        composeRule.setComponentContent {
            AppColumn {
                AppBadge("Primary")
                AppBadge("Secondary", variant = AppBadgeVariant.Secondary, compact = true)
                AppBadge("Tertiary", variant = AppBadgeVariant.Tertiary)
                AppBadge("Danger", variant = AppBadgeVariant.Destructive)
                AppBadge(
                    text = "Custom",
                    containerColor = androidx.compose.ui.graphics.Color.Black,
                    textColor = androidx.compose.ui.graphics.Color.White
                )
            }
        }

        listOf("Primary", "Secondary", "Tertiary", "Danger", "Custom").forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun appButton_enabledInvokesClickAndDisabledDoesNotExposeEnabledState() {
        var clicked = 0
        composeRule.setComponentContent {
            AppColumn {
                AppButton(text = "Salvar", onClick = { clicked++ }, modifier = Modifier.testTag("enabled-button"))
                AppButton(text = "Bloqueado", onClick = { clicked++ }, enabled = false, modifier = Modifier.testTag("disabled-button"))
            }
        }

        composeRule.onNodeWithTag("enabled-button").assertIsEnabled().performClick()
        composeRule.onNodeWithTag("disabled-button").assertIsNotEnabled()
        composeRule.runOnIdle { assertEquals(1, clicked) }
    }

    @Test
    fun appCallout_rendersTextAndSlotContent() {
        composeRule.setComponentContent {
            AppColumn {
                AppCallout(text = "Mensagem de alerta")
                AppCallout { Text("Conteudo customizado") }
            }
        }

        composeRule.onNodeWithText("Mensagem de alerta").assertIsDisplayed()
        composeRule.onNodeWithText("Conteudo customizado").assertIsDisplayed()
    }

    @Test
    fun appCard_rendersHeaderContentAndFooter() {
        composeRule.setComponentContent {
            AppCard {
                AppCardHeader {
                    AppCardTitle("Titulo")
                    AppCardSubtitle("Subtitulo")
                }
                AppCardContent {
                    Text("Corpo")
                }
                AppCardFooter {
                    Text("Rodape")
                }
            }
        }

        listOf("Titulo", "Subtitulo", "Corpo", "Rodape").forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun appCheckbox_togglesWhenEnabledAndIgnoresDisabledClick() {
        var enabledChecked by mutableStateOf(false)
        var disabledChecked by mutableStateOf(false)
        composeRule.setComponentContent {
            AppColumn {
                AppCheckbox(
                    checked = enabledChecked,
                    onCheckedChange = { enabledChecked = it },
                    label = "Aceito",
                    modifier = Modifier.testTag("enabled-checkbox")
                )
                AppCheckbox(
                    checked = disabledChecked,
                    onCheckedChange = { disabledChecked = it },
                    label = "Inativo",
                    enabled = false,
                    modifier = Modifier.testTag("disabled-checkbox")
                )
            }
        }

        composeRule.onNodeWithTag("enabled-checkbox").assertHasClickAction().performClick()
        composeRule.onNodeWithTag("disabled-checkbox").assertIsNotEnabled()
        composeRule.runOnIdle {
            assertTrue(enabledChecked)
            assertFalse(disabledChecked)
        }
    }

    @Test
    fun appDateInput_displaysValidDateAndPlaceholderForInvalidValue() {
        composeRule.setComponentContent {
            AppColumn {
                AppDateInput(value = "2026-05-25", onValueChange = {}, placeholder = "Data")
                AppDateInput(value = "2026-99-99", onValueChange = {}, placeholder = "Data invalida")
            }
        }

        composeRule.onNodeWithText("25 / 05 / 2026").assertIsDisplayed()
        composeRule.onNodeWithText("Data invalida").assertIsDisplayed()
    }

    @Test
    fun appForm_rendersLabelFieldAndError() {
        composeRule.setComponentContent {
            AppForm {
                AppFormField {
                    AppFormLabel("Email")
                    AppTextInput(value = "", onValueChange = {}, placeholder = "voce@email.com")
                    AppFormError("Campo obrigatorio")
                }
            }
        }

        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("voce@email.com").assertIsDisplayed()
        composeRule.onNodeWithText("Campo obrigatorio").assertIsDisplayed()
    }

    @Test
    fun appLayoutComponents_renderNestedContent() {
        composeRule.setComponentContent {
            AppRootContainer {
                AppColumn {
                    AppRow {
                        Text("Linha")
                        AppHorizontalSpacer(size = androidx.compose.ui.unit.dp(2))
                        Text("Coluna")
                    }
                    AppSpacer(size = androidx.compose.ui.unit.dp(2))
                    Text("Root")
                }
            }
        }

        listOf("Linha", "Coluna", "Root").forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun appSelect_selectsOptionAndDisabledSelectDoesNotOpenMenu() {
        var selected by mutableStateOf<String?>(null)
        composeRule.setComponentContent {
            AppColumn {
                AppSelect(
                    options = listOf(
                        AppSelectOption("Facil", "easy"),
                        AppSelectOption("Dificil", "hard")
                    ),
                    value = selected,
                    onValueChange = { selected = it },
                    placeholder = "Selecionar dificuldade"
                )
                AppSelect(
                    options = listOf(AppSelectOption("Oculto", "hidden")),
                    value = null,
                    onValueChange = {},
                    placeholder = "Desabilitado",
                    enabled = false
                )
            }
        }

        composeRule.onNodeWithText("Selecionar dificuldade").performClick()
        composeRule.onNodeWithText("Dificil").performClick()
        composeRule.runOnIdle { assertEquals("hard", selected) }
        composeRule.onNodeWithText("Desabilitado").assertIsDisplayed()
    }

    @Test
    fun appSpinner_exposesProgressSemantics() {
        composeRule.setComponentContent {
            AppSpinner(modifier = Modifier.testTag("spinner"))
        }

        composeRule.onNodeWithTag("spinner")
            .assertIsDisplayed()
            .assert(hasProgressBarRangeInfo())
    }

    @Test
    fun appTextInput_rendersPlaceholderAcceptsInputAndShowsValue() {
        var value by mutableStateOf("")
        composeRule.setComponentContent {
            AppTextInput(
                value = value,
                onValueChange = { value = it },
                placeholder = "Nome",
                modifier = Modifier.testTag("name-input")
            )
        }

        composeRule.onNodeWithText("Nome").assertIsDisplayed()
        composeRule.onNodeWithTag("name-input").performTextInput("Pedro")
        composeRule.onNodeWithTag("name-input").assertTextEquals("Pedro")
    }

    @Test
    fun appTypography_rendersAllTextStylesAndOverlineUppercase() {
        composeRule.setComponentContent {
            AppColumn {
                AppDisplay("Display")
                AppHero("Hero")
                AppMetric("Metric")
                AppTitle("Title")
                AppHeading("Heading")
                AppSectionTitle("Section")
                AppSubtitle("Subtitle")
                AppEmphasis("Emphasis")
                AppBody("Body")
                AppBodyStrong("BodyStrong")
                AppBodyMedium("BodyMedium")
                AppParagraph("Paragraph")
                AppSecondary("Secondary")
                AppSecondaryMedium("SecondaryMedium")
                AppSecondarySemiBold("SecondarySemiBold")
                AppButtonLabel("ButtonLabel")
                AppLabel("Label")
                AppLabelStrong("LabelStrong")
                AppTabLabel("TabLabel")
                AppTabLabelActive("TabLabelActive")
                AppOverline("overline")
                AppCaption("Caption")
                AppCaptionMuted("CaptionMuted")
                AppAccent("Accent")
            }
        }

        listOf(
            "Display", "Hero", "Metric", "Title", "Heading", "Section", "Subtitle",
            "Emphasis", "Body", "BodyStrong", "BodyMedium", "Paragraph", "Secondary",
            "SecondaryMedium", "SecondarySemiBold", "ButtonLabel", "Label", "LabelStrong",
            "TabLabel", "TabLabelActive", "OVERLINE", "Caption", "CaptionMuted", "Accent"
        ).forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun survivalShellComponents_renderExpectedContentAndInvokeActions() {
        var primaryClicks = 0
        var selectedTab = ""
        composeRule.setComponentContent {
            AppScreenScaffold {
                TopIdentityHeader(title = "Boa noite, Pedro", avatarName = "Pedro Barbosa", subtitle = "Online", badge = "ID")
                SurvivorAvatar(initials = "PB")
                StatusPill(text = "Ativo", tone = StatusPillTone.Success)
                SectionPillTabs(options = listOf("Hoje", "Semana"), selected = "Hoje")
                MetricStrip {
                    MetricCard(value = "10", label = "km", accent = "TOTAL")
                }
                FeatureCard(
                    eyebrow = "EYEBROW",
                    status = "STATUS",
                    title = "Feature",
                    body = "Body feature",
                    primaryAction = "Executar",
                    onPrimaryAction = { primaryClicks++ },
                    secondaryAction = "Cancelar",
                    footer = { Text("Footer") }
                )
                ListPanel(title = "Lista", actionLabel = "acao") {
                    ListRow(title = "Item", subtitle = "Sub", trailingTop = "Top", trailingBottom = "Bottom")
                }
                IconBadge(icon = Icons.Filled.Home, modifier = Modifier.testTag("icon-badge"))
                ProgressTrack(progress = 0.5f, modifier = Modifier.testTag("progress-track"))
                PanelDivider()
                PanelSpacer()
            }
            FloatingTabBar(currentTab = "home", onTabSelected = { selectedTab = it })
        }

        listOf(
            "Boa noite, Pedro", "Online", "ID", "PB", "Ativo", "Hoje", "Semana",
            "10", "km", "TOTAL", "EYEBROW", "STATUS", "Feature", "Body feature",
            "Executar", "Cancelar", "Footer", "Lista", "acao", "Item", "Sub", "Top", "Bottom",
            "Home", "RANKING", "PERFIL"
        ).forEach {
            composeRule.onNodeWithText(it, useUnmergedTree = true).assertIsDisplayed()
        }
        composeRule.onNodeWithTag("icon-badge").assertIsDisplayed()
        composeRule.onNodeWithTag("progress-track").assertIsDisplayed()
        composeRule.onNodeWithText("Executar").performClick()
        composeRule.onNodeWithContentDescription("Ranking").performClick()
        composeRule.runOnIdle {
            assertEquals(1, primaryClicks)
            assertEquals("rank", selectedTab)
        }
    }

    private fun ComposeContentTestRule.setComponentContent(content: @Composable () -> Unit) {
        setContent {
            TccMobileTheme {
                content()
            }
        }
    }
}
