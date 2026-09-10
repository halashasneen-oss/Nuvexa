package com.nuvexa.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/** Numeric fields accept localized digits/separators and normalize them to ASCII. */
@Composable
fun NuvexaNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    allowDecimal: Boolean = true,
    allowNegative: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> onValueChange(normalizeLocalizedNumberInput(input, allowDecimal, allowNegative)) },
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        suffix = suffix?.let { { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (allowDecimal) KeyboardType.Decimal else KeyboardType.Number),
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
fun NuvexaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        minLines = minLines,
        maxLines = maxLines,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

internal fun normalizeLocalizedNumberInput(input: String, allowDecimal: Boolean, allowNegative: Boolean): String {
    var hasDecimal = false
    val out = StringBuilder(input.length)
    input.forEach { original ->
        val c = when (original) {
            in '٠'..'٩' -> ('0'.code + (original.code - '٠'.code)).toChar()
            in '۰'..'۹' -> ('0'.code + (original.code - '۰'.code)).toChar()
            ',', '٫' -> '.'
            '−', '–', '—' -> '-'
            else -> original
        }
        when {
            c.isDigit() -> out.append(c)
            allowDecimal && c == '.' && !hasDecimal -> { hasDecimal = true; out.append(c) }
            allowNegative && c == '-' && out.isEmpty() -> out.append(c)
        }
    }
    return out.toString()
}
