package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun AppForm(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        content = content
    )
}

@Composable
fun AppFormField(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        content = content
    )
}

@Composable
fun AppFormLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = AppTheme.colors.textPrimary,
        fontSize = AppTheme.fontSize.lg
    )
}

@Composable
fun AppFormError(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = AppTheme.colors.destructive,
        fontSize = AppTheme.fontSize.sm
    )
}
