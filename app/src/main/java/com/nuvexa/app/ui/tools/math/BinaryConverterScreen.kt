package com.nuvexa.app.ui.tools.math

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import java.math.BigInteger

private data class BaseOption(val radix: Int, val labelRes: Int)

private val bases = listOf(
    BaseOption(2, R.string.binary_base_binary),
    BaseOption(10, R.string.binary_base_decimal),
    BaseOption(16, R.string.binary_base_hex),
    BaseOption(8, R.string.binary_base_octal),
)

@Composable
fun BinaryConverterScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var sourceRadix by remember { mutableStateOf(10) }
    var input by remember { mutableStateOf("42") }

    val parsed = remember(input, sourceRadix) {
        runCatching { BigInteger(input.trim(), sourceRadix) }.getOrNull()
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            bases.forEach { base ->
                FilterChip(
                    selected = sourceRadix == base.radix,
                    onClick = { sourceRadix = base.radix },
                    label = { Text(stringResource(base.labelRes)) },
                )
            }
        }

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text(stringResource(R.string.binary_input_value)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = input.isNotBlank() && parsed == null,
            textStyle = NuvexaExtraType.monospaceBody,
        )

        if (input.isNotBlank() && parsed == null) {
            Text(stringResource(R.string.binary_invalid_value), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        parsed?.let { value ->
            bases.filter { it.radix != sourceRadix }.forEach { base ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(base.labelRes), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value.toString(base.radix).let { if (base.radix == 16) it.uppercase() else it }, style = NuvexaExtraType.monospaceBody)
                }
            }
        }
    }
}
