package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun AppCallout(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppTheme.radii.md),
        color = AppTheme.colors.background,
        contentColor = AppTheme.colors.textSecondary,
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Row(
            modifier = Modifier.padding(AppTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            verticalAlignment = Alignment.Top,
            content = content
        )
    }
}

@Composable
fun AppCallout(
    text: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null
) {
    AppCallout(modifier = modifier) {
        icon?.invoke()
        Text(
            text = text,
            color = AppTheme.colors.textSecondary,
            fontSize = AppTheme.fontSize.md
        )
    }
}
