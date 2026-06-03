package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme

data class AppSelectOption(
    val label: String,
    val value: String
)

@Composable
fun AppSelect(
    options: List<AppSelectOption>,
    value: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Selecionar",
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = remember(options, value) {
        options.firstOrNull { it.value == value }
    }
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(AppTheme.radii.xs)

    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 40.dp)
                .clickable(enabled = enabled) { expanded = true },
            shape = shape,
            color = Color.Transparent,
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
                    text = selectedOption?.label ?: placeholder,
                    color = if (selectedOption == null) colors.placeholder else colors.textPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = AppTheme.fontSize.md
                )
                Text(
                    text = "v",
                    color = colors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = colors.card
        ) {
            options.forEach { option ->
                val selected = option.value == value

                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            color = if (selected) colors.textPrimary else colors.textSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = AppTheme.fontSize.md,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onValueChange(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}
