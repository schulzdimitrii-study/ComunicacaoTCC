package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme

enum class AppCheckboxSize {
    Small,
    Medium,
    Large,
    ExtraLarge
}

private fun resolveCheckboxSize(size: AppCheckboxSize): Dp {
    return when (size) {
        AppCheckboxSize.Small -> 8.dp
        AppCheckboxSize.Medium -> 12.dp
        AppCheckboxSize.Large -> 16.dp
        AppCheckboxSize.ExtraLarge -> 20.dp
    }
}

@Composable
fun AppCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    size: AppCheckboxSize = AppCheckboxSize.Medium,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.clickable(enabled = enabled) { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppCheckboxIndicator(checked = checked, size = size)
        if (label != null) {
            Text(
                text = label,
                color = AppTheme.colors.textPrimary,
                fontSize = AppTheme.fontSize.md
            )
        }
    }
}

@Composable
fun AppCheckboxIndicator(
    checked: Boolean,
    modifier: Modifier = Modifier,
    size: AppCheckboxSize = AppCheckboxSize.Medium
) {
    val indicatorSize = resolveCheckboxSize(size)
    val shape = RoundedCornerShape(AppTheme.radii.xxs)
    val checkColor = AppTheme.colors.textPrimary

    Box(
        modifier = modifier
            .size(indicatorSize)
            .background(if (checked) AppTheme.colors.primary else AppTheme.colors.background, shape)
            .border(
                BorderStroke(2.dp, if (checked) AppTheme.colors.primary else AppTheme.colors.border),
                shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Canvas(modifier = Modifier.size(indicatorSize * 0.62f)) {
                val canvasSize = this.size

                drawLine(
                    color = checkColor,
                    start = Offset(canvasSize.width * 0.12f, canvasSize.height * 0.52f),
                    end = Offset(canvasSize.width * 0.42f, canvasSize.height * 0.82f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = checkColor,
                    start = Offset(canvasSize.width * 0.42f, canvasSize.height * 0.82f),
                    end = Offset(canvasSize.width * 0.9f, canvasSize.height * 0.18f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
