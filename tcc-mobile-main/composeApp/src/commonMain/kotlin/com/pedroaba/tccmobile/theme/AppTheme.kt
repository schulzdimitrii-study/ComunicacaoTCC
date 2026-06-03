package com.pedroaba.tccmobile.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppThemeVariant {
    Crimson,
    Military,
    Signal,
    Burnt,
    Ash
}

@Immutable
data class AppPalette(
    val primary: Color,
    val deep: Color,
    val glow: Color,
    val secondary: Color,
    val primaryForeground: Color,
    val background: Color,
    val card: Color,
    val border: Color,
    val input: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val placeholder: Color,
    val destructive: Color,
    val destructiveForeground: Color,
    val textTertiary: Color
)

@Immutable
data class AppSpacing(
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp
)

@Immutable
data class AppRadii(
    val xxs: Dp = 4.dp,
    val xs: Dp = 6.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val twoXl: Dp = 24.dp,
    val threeXl: Dp = 28.dp,
    val fourXl: Dp = 32.dp,
    val fiveXl: Dp = 36.dp,
    val sixXl: Dp = 40.dp,
    val sevenXl: Dp = 44.dp,
    val eightXl: Dp = 48.dp,
    val nineXl: Dp = 52.dp,
    val tenXl: Dp = 56.dp,
    val full: Dp = 9999.dp
)

@Immutable
data class AppFontSize(
    val xs: TextUnit = 4.sp,
    val sm: TextUnit = 8.sp,
    val md: TextUnit = 12.sp,
    val lg: TextUnit = 16.sp,
    val xl: TextUnit = 20.sp,
    val xxl: TextUnit = 24.sp
)

@Immutable
data class AppThemeTokens(
    val variant: AppThemeVariant,
    val colors: AppPalette,
    val spacing: AppSpacing = AppSpacing(),
    val fontSize: AppFontSize = AppFontSize(),
    val radii: AppRadii = AppRadii()
)

private val CrimsonPalette = AppPalette(
    primary = Color(0xFFDC2626),
    deep = Color(0xFF7F1D1D),
    glow = Color(0xFFF87171),
    secondary = Color(0xFFFFF1F2),
    primaryForeground = Color(0xFFFFF1F2),
    background = Color(0xFF09090B),
    card = Color(0xFF111113),
    border = Color(0xFF27272A),
    input = Color(0xFF27272A),
    textPrimary = Color(0xFFFFF1F2),
    textSecondary = Color(0xFFA1A1AA),
    placeholder = Color(0xFF71717A),
    destructive = Color(0xFFE7000B),
    destructiveForeground = Color(0xFFFFFFFF),
    textTertiary = Color(0xFFFCA5A5)
)

private val MilitaryPalette = CrimsonPalette.copy(
    primary = Color(0xFF6F8446),
    deep = Color(0xFF3F4F2F),
    glow = Color(0xFFA3B57A),
    secondary = Color(0xFFF4F7EC),
    primaryForeground = Color(0xFFF4F7EC),
    textPrimary = Color(0xFFF4F7EC),
    textTertiary = Color(0xFFC3D5A1)
)

private val SignalPalette = CrimsonPalette.copy(
    primary = Color(0xFFCA8A04),
    deep = Color(0xFF6A5515),
    glow = Color(0xFFFACC15),
    secondary = Color(0xFFFFFBEA),
    primaryForeground = Color(0xFFFFFBEA),
    textPrimary = Color(0xFFFFFBEA),
    textTertiary = Color(0xFFFDE68A)
)

private val BurntPalette = CrimsonPalette.copy(
    primary = Color(0xFFC2410C),
    deep = Color(0xFF6B3410),
    glow = Color(0xFFFDBA74),
    secondary = Color(0xFFFFF7ED),
    primaryForeground = Color(0xFFFFF7ED),
    textPrimary = Color(0xFFFFF7ED),
    textTertiary = Color(0xFFFDBA74)
)

private val AshPalette = CrimsonPalette.copy(
    primary = Color(0xFFA1A1AA),
    deep = Color(0xFF52525B),
    glow = Color(0xFFE4E4E7),
    secondary = Color(0xFFF4F4F5),
    primaryForeground = Color(0xFFF4F4F5),
    textPrimary = Color(0xFFF4F4F5),
    textTertiary = Color(0xFFE4E4E7)
)

private fun paletteFor(variant: AppThemeVariant): AppPalette {
    return when (variant) {
        AppThemeVariant.Crimson -> CrimsonPalette
        AppThemeVariant.Military -> MilitaryPalette
        AppThemeVariant.Signal -> SignalPalette
        AppThemeVariant.Burnt -> BurntPalette
        AppThemeVariant.Ash -> AshPalette
    }
}

val LocalAppTheme = staticCompositionLocalOf {
    AppThemeTokens(
        variant = AppThemeVariant.Crimson,
        colors = CrimsonPalette
    )
}

object AppTheme {
    val tokens: AppThemeTokens
        @Composable get() = LocalAppTheme.current

    val colors: AppPalette
        @Composable get() = LocalAppTheme.current.colors

    val spacing: AppSpacing
        @Composable get() = LocalAppTheme.current.spacing

    val fontSize: AppFontSize
        @Composable get() = LocalAppTheme.current.fontSize

    val radii: AppRadii
        @Composable get() = LocalAppTheme.current.radii
}

@Composable
fun TccMobileTheme(
    variant: AppThemeVariant = AppThemeVariant.Crimson,
    content: @Composable () -> Unit
) {
    val palette = paletteFor(variant)
    val tokens = AppThemeTokens(variant = variant, colors = palette)

    androidx.compose.runtime.CompositionLocalProvider(LocalAppTheme provides tokens) {
        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = palette.primary,
                onPrimary = palette.primaryForeground,
                secondary = palette.secondary,
                onSecondary = palette.background,
                background = palette.background,
                onBackground = palette.textPrimary,
                surface = palette.card,
                onSurface = palette.textPrimary,
                surfaceVariant = palette.input,
                onSurfaceVariant = palette.textSecondary,
                outline = palette.border,
                error = palette.destructive,
                onError = palette.destructiveForeground
            ),
            typography = Typography(),
            shapes = Shapes(),
            content = content
        )
    }
}
