package com.pedroaba.tccmobile.features.auth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.pedroaba.tccmobile.ui.components.AppDateInput
import com.pedroaba.tccmobile.ui.components.AppForm
import com.pedroaba.tccmobile.ui.components.AppFormError
import com.pedroaba.tccmobile.ui.components.AppFormField
import com.pedroaba.tccmobile.ui.components.AppFormLabel
import com.pedroaba.tccmobile.ui.components.AppHero
import com.pedroaba.tccmobile.ui.components.AppRootContainer
import com.pedroaba.tccmobile.ui.components.AppSpinner
import com.pedroaba.tccmobile.ui.components.AppTextInput
import com.pedroaba.tccmobile.ui.format.parseHeightCmToMeters
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
    backendError: String? = null,
    onSignupRequested: (email: String, birthDate: String, name: String, height: Float?, weight: Float?, password: String) -> Unit = { _email: String, _birthDate: String, _name: String, _height: Float?, _weight: Float?, _password: String -> },
    onNavigateToLogin: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var birthDateError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun submit() {
        val trimmedEmail = email.trim()
        val trimmedName = name.trim()

        emailError = if (isValidEmail(trimmedEmail)) null else "E-mail inválido"
        nameError = if (trimmedName.isNotBlank()) null else "Nome inválido"
        passwordError = if (password.isNotBlank()) null else "Informe a senha"
        val parsedHeight = parseHeightCmToMeters(height)?.toFloat()
        val parsedWeight = weight.replace(",", ".").trim().ifBlank { null }?.toFloatOrNull()
        heightError = if (height.isBlank() || parsedHeight != null) null else "Informe a altura em cm."
        weightError = if (weight.isBlank() || parsedWeight != null) null else "Informe o peso em kg."

        if (emailError == null && nameError == null && passwordError == null && heightError == null && weightError == null) {
            onSignupRequested(
                trimmedEmail,
                birthDate,
                trimmedName,
                parsedHeight,
                parsedWeight,
                password
            )
        }
    }

    AppRootContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = AppTheme.spacing.xxl)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)
        ) {
            AppBadge("NOVO SOBREVIVENTE")

            Spacer(modifier = Modifier.weight(1f))

            AppHero("Crie sua conta.")
            AppBody("Cadastre os dados do usuário para personalizar zonas cardíacas e métricas iniciais.")

            AppCard {
                AppCardHeader {
                    AppCardTitle("Criar conta")
                    AppCardSubtitle("Preencha apenas os campos existentes no cadastro do usuário.")
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
                            AppFormLabel("Data de nascimento")
                            AppDateInput(
                                value = birthDate,
                                onValueChange = {
                                    birthDate = it
                                    birthDateError = null
                                },
                                placeholder = "DD / MM / AAAA"
                            )
                            birthDateError?.let { AppFormError(it) }
                        }

                        AppFormField {
                            AppFormLabel("Nome")
                            AppTextInput(
                                value = name,
                                onValueChange = {
                                    name = it
                                    nameError = null
                                },
                                placeholder = "Pedro Barbosa"
                            )
                            nameError?.let { AppFormError(it) }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                        ) {
                            AppFormField(
                                modifier = Modifier.weight(1f)
                            ) {
                                AppFormLabel("Altura")
                                AppTextInput(
                                    value = height,
                                    onValueChange = {
                                        height = it
                                        heightError = null
                                    },
                                    placeholder = "180 cm",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                                heightError?.let { AppFormError(it) }
                            }

                            AppFormField(
                                modifier = Modifier.weight(1f)
                            ) {
                                AppFormLabel("Peso")
                                AppTextInput(
                                    value = weight,
                                    onValueChange = {
                                        weight = it
                                        weightError = null
                                    },
                                    placeholder = "80 kg",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                                weightError?.let { AppFormError(it) }
                            }
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

                        AppButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = if (isSubmitting) "Criando conta..." else "Criar conta",
                            onClick = ::submit,
                            enabled = !isSubmitting,
                            leadingContent = {
                                if (isSubmitting) {
                                    AppSpinner(modifier = Modifier.size(16.dp))
                                }
                            }
                        )

                        AppButton(
                            text = "Voltar para login",
                            modifier = Modifier.fillMaxWidth(),
                            variant = AppButtonVariant.Outline,
                            onClick = onNavigateToLogin,
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar",
                                    modifier = Modifier.size(AppTheme.spacing.lg)
                                )
                            }
                        )
                    }
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
private fun SignupScreenPreview() {
    TccMobileTheme {
        SignupScreen()
    }
}
