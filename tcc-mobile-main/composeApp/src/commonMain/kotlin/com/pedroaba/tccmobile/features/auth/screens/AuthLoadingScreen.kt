package com.pedroaba.tccmobile.features.auth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme
import com.pedroaba.tccmobile.ui.components.AppBadge
import com.pedroaba.tccmobile.ui.components.AppBody
import com.pedroaba.tccmobile.ui.components.AppHero
import com.pedroaba.tccmobile.ui.components.AppRootContainer
import com.pedroaba.tccmobile.ui.components.AppSpinner

@Composable
fun AuthLoadingScreen() {
    AppRootContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBadge(text = "SINCRONIZANDO SESSÃO")
            Spacer(modifier = Modifier.height(AppTheme.spacing.xl))
            AppSpinner(modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(AppTheme.spacing.lg))
            AppHero("Carregando sobrevivente.")
            Spacer(modifier = Modifier.height(10.dp))
            AppBody("Verificando o JWT salvo neste dispositivo.")
        }
    }
}
