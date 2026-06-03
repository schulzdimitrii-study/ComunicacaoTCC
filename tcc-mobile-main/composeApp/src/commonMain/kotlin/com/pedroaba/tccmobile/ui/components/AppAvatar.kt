package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedroaba.tccmobile.theme.AppTheme

enum class AppAvatarSize {
    Small,
    Medium,
    Large
}

private fun resolveAvatarSize(size: AppAvatarSize): Dp {
    return when (size) {
        AppAvatarSize.Small -> 16.dp
        AppAvatarSize.Medium -> 24.dp
        AppAvatarSize.Large -> 32.dp
    }
}

@Composable
fun AppAvatar(
    fallback: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(AppTheme.colors.textTertiary)
            .border(BorderStroke(1.dp, AppTheme.colors.border), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = fallback.take(2).uppercase(),
            color = AppTheme.colors.deep,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AppAvatar(
    fallback: String,
    modifier: Modifier = Modifier,
    size: AppAvatarSize
) {
    AppAvatar(
        fallback = fallback,
        modifier = modifier,
        size = resolveAvatarSize(size)
    )
}
