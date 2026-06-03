package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun AppSpacer(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier.height(size))
}

@Composable
fun AppHorizontalSpacer(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier.width(size))
}

@Composable
fun AppRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(AppTheme.spacing.sm),
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
fun AppColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AppTheme.spacing.sm),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        content = content
    )
}

@Composable
fun AppRootContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = AppTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .safeDrawingPadding()
            .padding(AppTheme.spacing.lg)
    ) {
        content()
    }
}
