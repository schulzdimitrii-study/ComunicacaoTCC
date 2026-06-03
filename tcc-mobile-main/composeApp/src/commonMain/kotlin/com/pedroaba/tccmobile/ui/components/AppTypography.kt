package com.pedroaba.tccmobile.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
private fun AppText(
    text: String,
    fontSize: TextUnit,
    color: Color,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        lineHeight = lineHeight,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable fun AppDisplay(text: String, modifier: Modifier = Modifier) = AppText(text, 28.sp, AppTheme.colors.textPrimary, FontWeight.Bold, modifier)
@Composable fun AppHero(text: String, modifier: Modifier = Modifier) = AppText(text, 22.sp, AppTheme.colors.textPrimary, FontWeight.ExtraBold, modifier)
@Composable fun AppMetric(text: String, modifier: Modifier = Modifier) = AppText(text, 21.sp, AppTheme.colors.textPrimary, FontWeight.ExtraBold, modifier)
@Composable fun AppTitle(text: String, modifier: Modifier = Modifier) = AppText(text, 18.sp, AppTheme.colors.textPrimary, FontWeight.ExtraBold, modifier)
@Composable fun AppHeading(text: String, modifier: Modifier = Modifier) = AppText(text, 18.sp, AppTheme.colors.textPrimary, FontWeight.Bold, modifier)
@Composable fun AppSectionTitle(text: String, modifier: Modifier = Modifier) = AppText(text, 16.sp, AppTheme.colors.textPrimary, FontWeight.SemiBold, modifier)
@Composable fun AppSubtitle(text: String, modifier: Modifier = Modifier) = AppText(text, 14.sp, AppTheme.colors.textPrimary, FontWeight.Bold, modifier)
@Composable fun AppEmphasis(text: String, modifier: Modifier = Modifier) = AppText(text, 14.sp, AppTheme.colors.textPrimary, FontWeight.SemiBold, modifier)
@Composable fun AppBody(text: String, modifier: Modifier = Modifier) = AppText(text, 13.sp, AppTheme.colors.textSecondary, FontWeight.Normal, modifier, 18.sp)
@Composable fun AppBodyStrong(text: String, modifier: Modifier = Modifier) = AppText(text, 13.sp, AppTheme.colors.textPrimary, FontWeight.Bold, modifier)
@Composable fun AppBodyMedium(text: String, modifier: Modifier = Modifier) = AppText(text, 13.sp, AppTheme.colors.textSecondary, FontWeight.Medium, modifier)
@Composable fun AppParagraph(text: String, modifier: Modifier = Modifier) = AppText(text, 12.sp, AppTheme.colors.textSecondary, FontWeight.Normal, modifier, 16.sp)
@Composable fun AppSecondary(text: String, modifier: Modifier = Modifier) = AppText(text, 12.sp, AppTheme.colors.textSecondary, FontWeight.Normal, modifier)
@Composable fun AppSecondaryMedium(text: String, modifier: Modifier = Modifier) = AppText(text, 12.sp, AppTheme.colors.textSecondary, FontWeight.Medium, modifier)
@Composable fun AppSecondarySemiBold(text: String, modifier: Modifier = Modifier) = AppText(text, 12.sp, AppTheme.colors.textSecondary, FontWeight.SemiBold, modifier)
@Composable fun AppButtonLabel(text: String, modifier: Modifier = Modifier) = AppText(text, 12.sp, AppTheme.colors.primaryForeground, FontWeight.Bold, modifier)
@Composable fun AppLabel(text: String, modifier: Modifier = Modifier) = AppText(text, 11.sp, AppTheme.colors.textSecondary, FontWeight.Medium, modifier)
@Composable fun AppLabelStrong(text: String, modifier: Modifier = Modifier) = AppText(text, 11.sp, AppTheme.colors.textPrimary, FontWeight.Bold, modifier)
@Composable fun AppTabLabel(text: String, modifier: Modifier = Modifier) = AppText(text, 11.sp, AppTheme.colors.placeholder, FontWeight.SemiBold, modifier)
@Composable fun AppTabLabelActive(text: String, modifier: Modifier = Modifier) = AppText(text, 11.sp, AppTheme.colors.textPrimary, FontWeight.Bold, modifier)
@Composable fun AppOverline(text: String, modifier: Modifier = Modifier) = AppText(text.uppercase(), 10.sp, AppTheme.colors.placeholder, FontWeight.SemiBold, modifier)
@Composable fun AppCaption(text: String, modifier: Modifier = Modifier) = AppText(text, 11.sp, AppTheme.colors.textSecondary, FontWeight.Medium, modifier)
@Composable fun AppCaptionMuted(text: String, modifier: Modifier = Modifier) = AppText(text, 11.sp, AppTheme.colors.placeholder, FontWeight.SemiBold, modifier)
@Composable fun AppAccent(text: String, modifier: Modifier = Modifier) = AppText(text, 12.sp, AppTheme.colors.textTertiary, FontWeight.Bold, modifier)
