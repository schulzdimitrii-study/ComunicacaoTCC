package com.pedroaba.tccmobile.features.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import com.pedroaba.tccmobile.theme.AppTheme
import com.pedroaba.tccmobile.theme.TccMobileTheme
import com.pedroaba.tccmobile.ui.components.AppBadge
import com.pedroaba.tccmobile.ui.components.AppBody
import com.pedroaba.tccmobile.ui.components.AppButton
import com.pedroaba.tccmobile.ui.components.AppButtonVariant
import com.pedroaba.tccmobile.ui.components.AppCallout
import com.pedroaba.tccmobile.ui.components.AppCard
import com.pedroaba.tccmobile.ui.components.AppCardContent
import com.pedroaba.tccmobile.ui.components.AppCardFooter
import com.pedroaba.tccmobile.ui.components.AppCardHeader
import com.pedroaba.tccmobile.ui.components.AppCardSubtitle
import com.pedroaba.tccmobile.ui.components.AppCardTitle
import com.pedroaba.tccmobile.ui.components.AppCheckbox
import com.pedroaba.tccmobile.ui.components.AppForm
import com.pedroaba.tccmobile.ui.components.AppFormError
import com.pedroaba.tccmobile.ui.components.AppFormField
import com.pedroaba.tccmobile.ui.components.AppFormLabel
import com.pedroaba.tccmobile.ui.components.AppHero
import com.pedroaba.tccmobile.ui.components.AppRootContainer
import com.pedroaba.tccmobile.ui.components.AppSpinner
import com.pedroaba.tccmobile.ui.components.AppTextInput
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
    backendError: String? = null,
    onLoginRequested: (email: String, password: String, keepConnected: Boolean) -> Unit = { _, _, _ -> },
    onCreateProfileRequested: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var keepConnected by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun submit() {
        val trimmedEmail = email.trim()
        emailError = if (isValidEmail(trimmedEmail)) null else "E-mail inválido"
        passwordError = if (password.isNotBlank()) null else "Informe a senha"

        if (emailError == null && passwordError == null) {
            onLoginRequested(trimmedEmail, password, keepConnected)
        }
    }

    AppRootContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = AppTheme.spacing.xxl)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            AppBadge("ZONA VERMELHA ATIVA")

            Spacer(modifier = Modifier.weight(1f))

            AppHero("Entre com seu perfil.")
            AppBody("Acesse sua conta para sincronizar sessões, biométricos e progresso nas hordas.")

            AppCard {
                AppCardHeader {
                    AppCardTitle("Acessar conta")
                    AppCardSubtitle("Use email e senha cadastrados no app Bio Survival.")
                }

                AppCardContent(
                    modifier = Modifier.padding(vertical = AppTheme.spacing.xs)
                ) {
                    AppForm {
                        backendError?.let {
                            AppCallout(text = it)
                        }

                        AppFormField {
                            AppFormLabel("E-mail")
                            AppTextInput(
                                value = email,
                                onValueChange = {
                                    email = it
                                    emailError = null
                                },
                                placeholder = "ex: joao@gmail.com",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )
                            emailError?.let { AppFormError(it) }
                        }

                        AppFormField {
                            AppFormLabel("Senha")
                            AppTextInput(
                                value = password,
                                onValueChange = {
                                    password = it
                                    passwordError = null
                                },
                                placeholder = "********",
                                visualTransformation = if (isPasswordVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                                trailingContent = {
                                    IconButton(
                                        onClick = { isPasswordVisible = !isPasswordVisible },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) {
                                                Icons.Filled.VisibilityOff
                                            } else {
                                                Icons.Filled.Visibility
                                            },
                                            contentDescription = if (isPasswordVisible) {
                                                "Ocultar senha"
                                            } else {
                                                "Mostrar senha"
                                            },
                                            tint = AppTheme.colors.textSecondary
                                        )
                                    }
                                }
                            )
                            passwordError?.let { AppFormError(it) }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppCheckbox(
                                checked = keepConnected,
                                onCheckedChange = { keepConnected = it },
                                label = "Manter conectado"
                            )
                            AppBody("Esqueci minha senha")
                        }

                        AppButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = if (isSubmitting) "Entrando" else "Entrar",
                            onClick = ::submit,
                            enabled = !isSubmitting,
                            leadingContent = {
                                if (isSubmitting) {
                                    AppSpinner(modifier = Modifier.size(16.dp))
                                }
                            }
                        )

                        AppButton(
                            text = "Criar perfil atlético",
                            modifier = Modifier.fillMaxWidth(),
                            variant = AppButtonVariant.Outline,
                            onClick = onCreateProfileRequested
                        )
                    }
                }

                AppCardFooter {
                    AppCallout(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(AppTheme.colors.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                                    contentDescription = "ShowChart"
                                )
                            }
                        },
                        text = "Sem relógio conectado, você ainda registra tempo, distância e calorias estimadas."
                    )
                }
            }
        }
    }
}

private fun isValidEmail(value: String): Boolean {
    return value.contains("@") && value.substringAfter("@").contains(".")
}

@Preview
@Composable
private fun LoginScreenPreview() {
    TccMobileTheme {
        LoginScreen()
    }
}
