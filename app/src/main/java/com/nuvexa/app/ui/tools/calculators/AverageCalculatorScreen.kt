package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.core.util.formatResultNumber
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing

private data class AverageResult(val mean: Double, val min: Double, val max: Double, val count: Int)

private fun parseNumbers(input: String): List<Double> =
    input.split(Regex("[,\\n]")).mapNotNull { it.trim().toDoubleOrNull() }

@Composable
fun AverageCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<AverageResult?>(null) }

    fun calculate() {
        val numbers = parseNumbers(input)
        if (numbers.isEmpty()) return
        result = AverageResult(numbers.average(), numbers.min(), numbers.max(), numbers.size)
        onResult("Average of ${numbers.size} numbers = ${formatResultNumber(numbers.average())}")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = input, onValueChange = { input = it; result = null }, label = stringResource(R.string.average_numbers_input), minLines = 4)

        PrimaryButton(text = stringResource(R.string.action_calculate), onClick = ::calculate, modifier = Modifier.fillMaxWidth(), enabled = input.isNotBlank())

        result?.let { r ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = formatResultNumber(r.mean), label = stringResource(R.string.average_result_mean))
                Text(
                    "${stringResource(R.string.average_result_min)}: ${formatResultNumber(r.min)}  •  " +
                        "${stringResource(R.string.average_result_max)}: ${formatResultNumber(r.max)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    stringResource(R.string.average_count, r.count),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
