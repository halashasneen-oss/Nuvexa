package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
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
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import java.math.BigDecimal

private enum class PercentMode { OF, CHANGE }

@Composable
fun PercentageCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var mode by remember { mutableStateOf(PercentMode.OF) }
    var percent by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<BigDecimal?>(null) }

    fun calculate() {
        val percentValue = percent.toBigDecimalOrNull() ?: return
        val baseValue = value.toBigDecimalOrNull() ?: return
        result = when (mode) {
            PercentMode.OF -> baseValue * percentValue / BigDecimal(100)
            PercentMode.CHANGE -> baseValue + (baseValue * percentValue / BigDecimal(100))
        }
        result?.let { onResult("$percent% of $value = ${formatResultNumber(it)}") }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            FilterChip(
                selected = mode == PercentMode.OF,
                onClick = { mode = PercentMode.OF; result = null },
                label = { Text(stringResource(R.string.tool_calc_percentage_name)) },
            )
            FilterChip(
                selected = mode == PercentMode.CHANGE,
                onClick = { mode = PercentMode.CHANGE; result = null },
                label = { Text("+/−") },
            )
        }

        NuvexaNumberField(value = percent, onValueChange = { percent = it; result = null }, label = "%", suffix = "%")
        NuvexaNumberField(value = value, onValueChange = { value = it; result = null }, label = stringResource(R.string.tool_unit_converter_name))

        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = percent.isNotBlank() && value.isNotBlank(),
        )

        result?.let { r ->
            val formula = when (mode) {
                PercentMode.OF -> "$percent% × $value"
                PercentMode.CHANGE -> "$value ${if ((percent.toBigDecimalOrNull() ?: BigDecimal.ZERO) >= BigDecimal.ZERO) "+" else "−"} $percent%"
            }
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                Text(formula, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                ResultCard(value = formatResultNumber(r))
            }
        }
    }
}
