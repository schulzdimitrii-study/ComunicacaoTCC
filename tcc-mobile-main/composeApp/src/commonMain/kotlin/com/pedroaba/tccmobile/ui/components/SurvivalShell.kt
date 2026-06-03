package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun AppScreenScaffold(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    AppRootContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = AppTheme.spacing.sm)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 84.dp),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                content = content
            )
        }
    }
}

@Composable
fun TopIdentityHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    avatarName: String? = null,
    badge: String? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            SurvivorAvatar(initials = avatarInitials(avatarName ?: title))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AppTitle(title)
                AppSecondary(subtitle)
                badge?.let { AppBadge(text = it, variant = AppBadgeVariant.Secondary, compact = true) }
            }
        }
        trailing?.invoke(this)
    }
}

private fun avatarInitials(name: String): String =
    name
        .trim()
        .split(Regex("\\s+"))
        .mapNotNull { part -> part.firstOrNull { it.isLetterOrDigit() }?.uppercase() }
        .take(2)
        .joinToString("")

@Composable
fun SurvivorAvatar(
    initials: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        AppTheme.colors.primary,
                        AppTheme.colors.deep
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AppLabelStrong(initials.ifBlank { "SV" })
    }
}

@Composable
fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
    tone: StatusPillTone = StatusPillTone.Primary
) {
    val background = when (tone) {
        StatusPillTone.Primary -> AppTheme.colors.deep
        StatusPillTone.Neutral -> AppTheme.colors.input
        StatusPillTone.Success -> Color(0xFF173F2B)
        StatusPillTone.Alert -> Color(0xFF4C1D1D)
    }
    val foreground = when (tone) {
        StatusPillTone.Neutral -> AppTheme.colors.textSecondary
        StatusPillTone.Success -> Color(0xFF86EFAC)
        StatusPillTone.Alert -> Color(0xFFFDA4AF)
        StatusPillTone.Primary -> AppTheme.colors.textPrimary
    }
    AppBadge(
        text = text,
        modifier = modifier,
        containerColor = background,
        textColor = foreground
    )
}

enum class StatusPillTone {
    Primary,
    Neutral,
    Success,
    Alert
}

@Composable
fun SectionPillTabs(
    options: List<String>,
    selected: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
    ) {
        options.forEach { option ->
            val active = option == selected
            Surface(
                shape = RoundedCornerShape(AppTheme.radii.full),
                color = if (active) AppTheme.colors.primary else AppTheme.colors.card,
                contentColor = if (active) AppTheme.colors.primaryForeground else AppTheme.colors.textSecondary,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (active) AppTheme.colors.primary else AppTheme.colors.border)
            ) {
                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                    if (active) {
                        AppCaption(text = option)
                    } else {
                        AppCaptionMuted(text = option)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricStrip(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        content = content
    )
}

@Composable
fun MetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accent: String? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = AppTheme.colors.card,
        contentColor = AppTheme.colors.textPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            accent?.let { AppOverline(it) }
            AppMetric(value)
            AppCaption(label)
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    status: String? = null,
    statusTone: StatusPillTone = StatusPillTone.Primary,
    primaryAction: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    primaryActionEnabled: Boolean = true,
    secondaryAction: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryActionEnabled: Boolean = true,
    footer: (@Composable ColumnScope.() -> Unit)? = null
) {
    AppCard(modifier = modifier) {
        eyebrow?.let { AppOverline(it) }
        status?.let { StatusPill(text = status, tone = statusTone) }
        AppTitle(title)
        AppBody(body)
        if (primaryAction != null || secondaryAction != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                primaryAction?.let {
                    AppButton(
                        text = it,
                        onClick = { onPrimaryAction?.invoke() },
                        modifier = Modifier.weight(if (secondaryAction != null) 1.35f else 1f),
                        enabled = primaryActionEnabled
                    )
                }
                secondaryAction?.let {
                    AppButton(
                        text = it,
                        onClick = { onSecondaryAction?.invoke() },
                        modifier = Modifier.weight(1f),
                        variant = AppButtonVariant.Outline,
                        enabled = secondaryActionEnabled
                    )
                }
            }
        }
        footer?.invoke(this)
    }
}

@Composable
fun ListPanel(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTitle(title)
            actionLabel?.let { AppAccent(it) }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            content = content
        )
    }
}

@Composable
fun ListRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailingTop: String? = null,
    trailingBottom: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable ColumnScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading?.invoke()
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            AppEmphasis(title)
            AppCaption(subtitle)
        }
        if (trailing != null) {
            Column(horizontalAlignment = Alignment.End, content = trailing)
        } else if (trailingTop != null || trailingBottom != null) {
            Column(horizontalAlignment = Alignment.End) {
                trailingTop?.let { AppSecondarySemiBold(it) }
                trailingBottom?.let { AppCaption(it) }
            }
        }
    }
}

@Composable
fun IconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = AppTheme.colors.textSecondary,
    background: Color = AppTheme.colors.input
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun ProgressTrack(
    progress: Float,
    modifier: Modifier = Modifier,
    accent: Color = AppTheme.colors.primary
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(AppTheme.radii.full))
            .background(AppTheme.colors.input)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(RoundedCornerShape(AppTheme.radii.full))
                .background(accent)
        )
    }
}

@Composable
fun PanelDivider() {
    HorizontalDivider(color = AppTheme.colors.border)
}

@Composable
fun PanelSpacer() {
    Spacer(modifier = Modifier.height(2.dp))
}
