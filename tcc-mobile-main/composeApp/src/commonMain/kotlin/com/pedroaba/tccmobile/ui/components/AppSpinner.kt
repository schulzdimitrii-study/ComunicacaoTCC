package com.pedroaba.tccmobile.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun AppSpinner(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = AppTheme.colors.primaryForeground,
        strokeWidth = strokeWidth
    )
}
