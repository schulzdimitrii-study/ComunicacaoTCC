package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.pedroaba.tccmobile.theme.AppTheme

enum class AppBadgeVariant {
    Primary,
    Secondary,
    Tertiary,
    Destructive
}

@Composable
fun AppBadge(
    modifier: Modifier = Modifier,
    variant: AppBadgeVariant = AppBadgeVariant.Primary,
    compact: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val colors = AppTheme.colors
    val background = when (variant) {
        AppBadgeVariant.Primary -> colors.deep
        AppBadgeVariant.Secondary -> colors.secondary
        AppBadgeVariant.Tertiary -> colors.textTertiary
        AppBadgeVariant.Destructive -> colors.destructive
    }
    val contentColor = when (variant) {
        AppBadgeVariant.Secondary -> colors.background
        else -> colors.textPrimary
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AppTheme.radii.full),
        color = background,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) AppTheme.spacing.md else AppTheme.spacing.lg,
                vertical = if (compact) AppTheme.spacing.xs else AppTheme.spacing.xs
            ),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun AppBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: AppBadgeVariant = AppBadgeVariant.Primary,
    compact: Boolean = false,
    fontSize: TextUnit = if (compact) AppTheme.fontSize.sm else AppTheme.fontSize.md
) {
    AppBadge(modifier = modifier, variant = variant, compact = compact) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.Unspecified
        )
    }
}

@Composable
fun AppBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color,
    textColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AppTheme.radii.full),
        color = containerColor,
        contentColor = textColor
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.lg,
                vertical = AppTheme.spacing.xs
            ),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = AppTheme.fontSize.md,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
