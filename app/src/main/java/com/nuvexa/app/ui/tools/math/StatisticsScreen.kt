package com.nuvexa.app.ui.tools.math

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import kotlin.math.sqrt

private data class StatisticsResult(
    val mean: Double,
    val median: Double,
    val mode: String,
    val variance: Double,
    val stdDev: Double,
)

private fun computeStatistics(numbers: List<Double>): StatisticsResult {
    val mean = numbers.average()
    val sorted = numbers.sorted()
    val median = if (sorted.size % 2 == 0) {
        (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2
    } else {
        sorted[sorted.size / 2]
    }
    val frequency = numbers.groupingBy { it }.eachCount()
    val maxFrequency = frequency.values.max()
    val mode = if (maxFrequency <= 1) {
        "—"
    } else {
        frequency.filterValues { it == maxFrequency }.keys.sorted().joinToString(", ") { formatResultNumber(it) }
    }
    val variance = numbers.sumOf { (it - mean) * (it - mean) } / numbers.size
    return StatisticsResult(mean, median, mode, variance, sqrt(variance))
}

@Composable
fun StatisticsScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<StatisticsResult?>(null) }

    fun calculate() {
        val numbers = input.split(Regex("[,\\n]")).mapNotNull { it.trim().toDoubleOrNull() }
        if (numbers.isEmpty()) return
        result = computeStatistics(numbers)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = input, onValueChange = { input = it; result = null }, label = stringResource(R.string.statistics_input), minLines = 4)
        PrimaryButton(text = stringResource(R.string.action_calculate), onClick = ::calculate, modifier = Modifier.fillMaxWidth(), enabled = input.isNotBlank())

        result?.let { r ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = formatResultNumber(r.mean), label = stringResource(R.string.statistics_mean))
                listOf(
                    stringResource(R.string.statistics_median) to formatResultNumber(r.median),
                    stringResource(R.string.statistics_mode) to r.mode,
                    stringResource(R.string.statistics_variance) to formatResultNumber(r.variance),
                    stringResource(R.string.statistics_std_dev) to formatResultNumber(r.stdDev),
                ).forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
