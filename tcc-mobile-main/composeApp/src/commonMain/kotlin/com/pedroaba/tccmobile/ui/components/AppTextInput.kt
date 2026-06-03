package com.pedroaba.tccmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun AppTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(AppTheme.radii.xs)
    val textStyle = LocalTextStyle.current.copy(
        color = colors.textPrimary,
        fontSize = AppTheme.fontSize.md
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = singleLine,
        textStyle = textStyle,
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        interactionSource = remember { MutableInteractionSource() },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp)
                    .background(colors.background, shape)
                    .border(BorderStroke(1.dp, colors.border), shape)
                    .padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingContent?.invoke()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = if (leadingContent != null) AppTheme.spacing.sm else 0.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = colors.placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = AppTheme.fontSize.md
                        )
                    }
                    innerTextField()
                }
                trailingContent?.invoke()
            }
        }
    )
}
