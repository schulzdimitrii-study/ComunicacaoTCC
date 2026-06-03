package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme

enum class AppDateOutputFormat {
    Display,
    Iso
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDateInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "DD / MM / AAAA",
    outputFormat: AppDateOutputFormat = AppDateOutputFormat.Display
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    val selectedMillis = remember(value) { parseDateToEpochMillis(value) }
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(AppTheme.radii.xs)
    val displayValue = formatForDisplay(value)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 40.dp)
            .clickable { isDialogOpen = true },
        shape = shape,
        color = colors.background,
        contentColor = colors.textPrimary,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.lg,
                vertical = AppTheme.spacing.sm
            ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayValue ?: placeholder,
                color = if (displayValue == null) colors.placeholder else colors.textPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = AppTheme.fontSize.md
            )
            Text(
                text = "Selecionar",
                color = colors.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = AppTheme.fontSize.sm,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (isDialogOpen) {
        val datePickerState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = selectedMillis
        )

        DatePickerDialog(
            onDismissRequest = { isDialogOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis
                            ?.let { formatEpochMillis(it, outputFormat) }
                            ?.let(onValueChange)
                        isDialogOpen = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogOpen = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

private fun formatForDisplay(value: String): String? {
    val date = parseDate(value) ?: return null
    return "${date.day.twoDigits()} / ${date.month.twoDigits()} / ${date.year}"
}

private fun formatEpochMillis(epochMillis: Long, outputFormat: AppDateOutputFormat): String {
    val date = civilFromEpochDay(floorDiv(epochMillis, MillisPerDay))
    return when (outputFormat) {
        AppDateOutputFormat.Display -> "${date.day.twoDigits()} / ${date.month.twoDigits()} / ${date.year}"
        AppDateOutputFormat.Iso -> "${date.year}-${date.month.twoDigits()}-${date.day.twoDigits()}"
    }
}

private fun parseDateToEpochMillis(value: String): Long? {
    val date = parseDate(value) ?: return null
    return epochDayFromCivil(date.year, date.month, date.day) * MillisPerDay
}

private fun parseDate(value: String): DateParts? {
    val trimmed = value.trim()
    if (trimmed.isBlank()) return null

    val parts = when {
        "-" in trimmed -> trimmed.split("-").mapNotNull { it.toIntOrNull() }
        else -> {
            val digits = trimmed.filter(Char::isDigit)
            if (digits.length != 8) return null
            listOf(
                digits.drop(4).toIntOrNull(),
                digits.drop(2).take(2).toIntOrNull(),
                digits.take(2).toIntOrNull()
            ).mapNotNull { it }
        }
    }

    if (parts.size != 3) return null
    val date = DateParts(year = parts[0], month = parts[1], day = parts[2])
    return date.takeIf { it.isValid() }
}

private fun DateParts.isValid(): Boolean {
    if (year !in 1900..2200) return false
    if (month !in 1..12) return false
    return day in 1..daysInMonth(year, month)
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    2 -> if (isLeapYear(year)) 29 else 28
    4, 6, 9, 11 -> 30
    else -> 31
}

private fun isLeapYear(year: Int): Boolean =
    year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

private fun epochDayFromCivil(year: Int, month: Int, day: Int): Long {
    var y = year
    val m = month
    y -= if (m <= 2) 1 else 0
    val era = floorDiv(y.toLong(), 400L)
    val yearOfEra = y - (era * 400L).toInt()
    val monthPrime = m + if (m > 2) -3 else 9
    val dayOfYear = (153 * monthPrime + 2) / 5 + day - 1
    val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
    return era * 146097L + dayOfEra - 719468L
}

private fun civilFromEpochDay(epochDay: Long): DateParts {
    val z = epochDay + 719468L
    val era = floorDiv(z, 146097L)
    val dayOfEra = (z - era * 146097L).toInt()
    val yearOfEra = (dayOfEra - dayOfEra / 1460 + dayOfEra / 36524 - dayOfEra / 146096) / 365
    var year = yearOfEra + (era * 400L).toInt()
    val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
    val monthPrime = (5 * dayOfYear + 2) / 153
    val day = dayOfYear - (153 * monthPrime + 2) / 5 + 1
    val month = monthPrime + if (monthPrime < 10) 3 else -9
    year += if (month <= 2) 1 else 0
    return DateParts(year = year, month = month, day = day)
}

private fun floorDiv(value: Long, divisor: Long): Long {
    val quotient = value / divisor
    val remainder = value % divisor
    return if (remainder != 0L && (value xor divisor) < 0) quotient - 1 else quotient
}

private fun Int.twoDigits(): String = if (this < 10) "0$this" else toString()

private const val MillisPerDay = 86_400_000L

private data class DateParts(
    val year: Int,
    val month: Int,
    val day: Int
)
