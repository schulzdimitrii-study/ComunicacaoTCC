package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme

enum class AppButtonVariant {
    Default,
    Outline,
    Destructive
}

@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.Default,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val colors = AppTheme.colors
    val containerColor = when (variant) {
        AppButtonVariant.Default -> colors.primary
        AppButtonVariant.Outline -> Color.Transparent
        AppButtonVariant.Destructive -> colors.destructive
    }
    val contentColor = when (variant) {
        AppButtonVariant.Default -> colors.primaryForeground
        AppButtonVariant.Outline -> colors.textPrimary
        AppButtonVariant.Destructive -> colors.destructiveForeground
    }
    val borderColor = when (variant) {
        AppButtonVariant.Default -> colors.primary
        AppButtonVariant.Outline -> colors.border
        AppButtonVariant.Destructive -> colors.destructive
    }

    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .alpha(if (enabled) 1f else 0.56f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        shape = RoundedCornerShape(AppTheme.radii.xs),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.lg,
                vertical = AppTheme.spacing.sm
            ),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.Default,
    enabled: Boolean = true,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    AppButton(
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        enabled = enabled
    ) {
        leadingContent?.invoke()
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = AppTheme.fontSize.md,
            fontWeight = FontWeight.Medium,
            color = LocalContentColor.current
        )
        trailingContent?.invoke()
    }
}
