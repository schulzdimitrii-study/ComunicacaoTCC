package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppTheme.radii.sm),
        color = AppTheme.colors.card,
        contentColor = AppTheme.colors.textPrimary,
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            content = content
        )
    }
}

@Composable
fun AppCardHeader(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
        content = content
    )
}

@Composable
fun AppCardTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = AppTheme.colors.textPrimary,
        style = MaterialTheme.typography.titleMedium,
        fontSize = AppTheme.fontSize.lg,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun AppCardSubtitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = AppTheme.colors.textSecondary,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = AppTheme.fontSize.md
    )
}

@Composable
fun AppCardContent(
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
fun AppCardFooter(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        content = content
    )
}
