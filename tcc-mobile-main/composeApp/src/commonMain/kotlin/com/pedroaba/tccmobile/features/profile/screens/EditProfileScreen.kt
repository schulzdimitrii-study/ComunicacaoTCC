package com.pedroaba.tccmobile.features.profile.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.backend.model.UpdateUserProfileRequest
import com.pedroaba.tccmobile.backend.online.UserProfileState
import com.pedroaba.tccmobile.backend.online.UserProfileStatus
import com.pedroaba.tccmobile.ui.components.AppButton
import com.pedroaba.tccmobile.ui.components.AppButtonVariant
import com.pedroaba.tccmobile.ui.components.AppCard
import com.pedroaba.tccmobile.ui.components.AppCaption
import com.pedroaba.tccmobile.ui.components.AppCallout
import com.pedroaba.tccmobile.ui.components.AppDateInput
import com.pedroaba.tccmobile.ui.components.AppDateOutputFormat
import com.pedroaba.tccmobile.ui.components.AppForm
import com.pedroaba.tccmobile.ui.components.AppFormError
import com.pedroaba.tccmobile.ui.components.AppFormField
import com.pedroaba.tccmobile.ui.components.AppFormLabel
import com.pedroaba.tccmobile.ui.components.AppScreenScaffold
import com.pedroaba.tccmobile.ui.components.AppSecondary
import com.pedroaba.tccmobile.ui.components.AppTextInput
import com.pedroaba.tccmobile.ui.components.AppTitle
import com.pedroaba.tccmobile.ui.components.TopIdentityHeader
import com.pedroaba.tccmobile.ui.format.formatHeightMetersToCm
import com.pedroaba.tccmobile.ui.format.parseHeightCmToMeters

@Composable
fun EditProfileScreen(
    userId: String = "",
    userName: String = "",
    userEmail: String = "",
    userProfileState: UserProfileState = UserProfileState(),
    onSaveProfile: (UpdateUserProfileRequest) -> Unit = {},
    onBack: () -> Unit = {},
    onTabSelected: (String) -> Unit = {}
) {
    val profile = userProfileState.profile
    var name by remember { mutableStateOf(profile?.name ?: userName) }
    var email by remember { mutableStateOf(profile?.email ?: userEmail) }
    var birthdayDate by remember { mutableStateOf(profile?.birthdayDate.orEmpty()) }
    var height by remember { mutableStateOf(profile?.height?.let(::formatHeightMetersToCm).orEmpty()) }
    var weight by remember { mutableStateOf(profile?.weight?.toString().orEmpty()) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val isSaving = userProfileState.status == UserProfileStatus.SAVING

    LaunchedEffect(profile?.id, userId) {
        name = profile?.name ?: userName
        email = profile?.email ?: userEmail
        birthdayDate = profile?.birthdayDate.orEmpty()
        height = profile?.height?.let(::formatHeightMetersToCm).orEmpty()
        weight = profile?.weight?.toString().orEmpty()
        validationError = null
    }

    AppScreenScaffold {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            AppTitle("Editar perfil")
            AppSecondary("Atualize os campos aceitos pelo backend para o usuário autenticado.")
        }

        AppCard {
            TopIdentityHeader(
                title = name.ifBlank { userName.ifBlank { "Perfil" } },
                subtitle = userId.ifBlank { "ID do usuário indisponível" }
            )
        }

        userProfileState.errorMessage?.let { AppCallout(text = it) }

        AppCard {
            AppForm {
                AppFormField {
                    AppFormLabel("Nome")
                    AppTextInput(value = name, onValueChange = { name = it }, placeholder = "Nome completo")
                }
                AppFormField {
                    AppFormLabel("E-mail")
                    AppTextInput(value = email, onValueChange = { email = it }, placeholder = "voce@email.com")
                }
                AppFormField {
                    AppFormLabel("Data de nascimento")
                    AppDateInput(
                        value = birthdayDate,
                        onValueChange = { birthdayDate = it },
                        placeholder = "Selecionar data",
                        outputFormat = AppDateOutputFormat.Iso
                    )
                }
                AppFormField {
                    AppFormLabel("Altura")
                    AppTextInput(
                        value = height,
                        onValueChange = { height = it },
                        placeholder = "Ex: 176",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    AppCaption("Use centímetros.")
                }
                AppFormField {
                    AppFormLabel("Peso")
                    AppTextInput(
                        value = weight,
                        onValueChange = { weight = it },
                        placeholder = "Ex: 74.5",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    AppCaption("Use quilogramas.")
                }
                validationError?.let { AppFormError(it) }
            }
        }

        AppCard {
            AppTitle("Campos sincronizados")
            AppCaption("Nome, e-mail, nascimento, altura e peso.")
            AppSecondary("A FC máxima é calculada automaticamente pela data de nascimento.")
        }

        Column(verticalArrangement = Arrangement.spacedBy(com.pedroaba.tccmobile.theme.AppTheme.spacing.sm)) {
            AppButton(
                text = if (isSaving) "Salvando..." else "Salvar alterações",
                onClick = {
                    val request = buildUpdateRequest(
                        name = name,
                        email = email,
                        birthdayDate = birthdayDate,
                        height = height,
                        weight = weight,
                        onInvalid = { validationError = it }
                    ) ?: return@AppButton

                    validationError = null
                    onSaveProfile(request)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            )
            AppButton(
                text = "Voltar ao perfil",
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                variant = AppButtonVariant.Outline
            )
        }
    }
}

private fun buildUpdateRequest(
    name: String,
    email: String,
    birthdayDate: String,
    height: String,
    weight: String,
    onInvalid: (String) -> Unit
): UpdateUserProfileRequest? {
    if (name.isBlank()) {
        onInvalid("Informe o nome.")
        return null
    }
    if (email.isBlank()) {
        onInvalid("Informe o e-mail.")
        return null
    }

    val normalizedBirthDate = birthdayDate.trim().ifBlank { null }
    if (normalizedBirthDate != null && !Regex("""\d{4}-\d{2}-\d{2}""").matches(normalizedBirthDate)) {
        onInvalid("Use a data no formato AAAA-MM-DD.")
        return null
    }

    val parsedHeight = parseHeightCmToMeters(height)
    if (height.isNotBlank() && parsedHeight == null) {
        onInvalid("Informe a altura em centímetros.")
        return null
    }

    val parsedWeight = weight.replace(",", ".").trim().ifBlank { null }?.toDoubleOrNull()
    if (weight.isNotBlank() && parsedWeight == null) {
        onInvalid("Informe o peso em kg usando ponto decimal.")
        return null
    }

    return UpdateUserProfileRequest(
        name = name.trim(),
        email = email.trim(),
        birthdayDate = normalizedBirthDate,
        maxHeartRate = null,
        height = parsedHeight,
        weight = parsedWeight
    )
}
