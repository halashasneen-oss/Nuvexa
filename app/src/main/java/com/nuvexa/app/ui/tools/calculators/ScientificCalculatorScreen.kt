package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.core.util.ScientificExpression
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import java.math.BigDecimal
import kotlin.math.abs

@Composable
fun ScientificCalculatorScreen(
    modifier: Modifier = Modifier,
    recordHistory: (String) -> Unit = {},
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var expression by remember { mutableStateOf("") }
    var useDegrees by remember { mutableStateOf(true) }
    var result by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val invalidMessage = stringResource(R.string.scientific_invalid_expression)

    fun evaluate() {
        runCatching { ScientificExpression.evaluate(expression, useDegrees) }
            .onSuccess { value ->
                val formatted = formatScientificResult(value)
                result = formatted
                error = null
                recordHistory("$expression = $formatted")
            }
            .onFailure {
                result = null
                error = invalidMessage
            }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.l),
    ) {
        OutlinedTextField(
            value = expression,
            onValueChange = { expression = it; error = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.scientific_expression)) },
            placeholder = { Text("sin(30) + sqrt(16) × 2") },
            minLines = 2,
            maxLines = 4,
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            FilterChip(
                selected = useDegrees,
                onClick = { useDegrees = true },
                label = { Text(stringResource(R.string.scientific_degrees)) },
            )
            FilterChip(
                selected = !useDegrees,
                onClick = { useDegrees = false },
                label = { Text(stringResource(R.string.scientific_radians)) },
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Text(
                text = stringResource(R.string.scientific_quick_functions),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            listOf(
                listOf("sin(", "cos(", "tan(", "sqrt("),
                listOf("log(", "ln(", "π", "^"),
            ).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    rowItems.forEach { token ->
                        AssistChip(
                            onClick = { expression += token },
                            label = { Text(token) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        PrimaryButton(
            text = stringResource(R.string.scientific_evaluate),
            onClick = ::evaluate,
            enabled = expression.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.Calculate, contentDescription = null) },
        )

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        result?.let { value ->
            ResultCard(
                value = value,
                label = stringResource(R.string.scientific_result),
                onCopy = { context.copyTextToClipboard(value) },
            )
        }
    }
}

private fun formatScientificResult(value: Double): String {
    val magnitude = abs(value)
    return if (magnitude != 0.0 && (magnitude >= 1e12 || magnitude < 1e-8)) {
        "%1$.8e".format(java.util.Locale.US, value)
    } else {
        BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
    }
}
