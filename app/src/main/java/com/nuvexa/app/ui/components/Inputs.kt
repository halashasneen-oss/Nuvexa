package com.nuvexa.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

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
        onValueChange = { input ->
            val filtered = input.filterIndexed { index, c ->
                c.isDigit() ||
                    (allowDecimal && c == '.' && input.indexOf('.') == index) ||
                    (allowNegative && c == '-' && index == 0)
            }
            onValueChange(filtered)
        },
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        suffix = suffix?.let { { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (allowDecimal) KeyboardType.Decimal else KeyboardType.Number,
        ),
        shape = MaterialTheme.shapes.medium,
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
        shape = MaterialTheme.shapes.medium,
    )
}
