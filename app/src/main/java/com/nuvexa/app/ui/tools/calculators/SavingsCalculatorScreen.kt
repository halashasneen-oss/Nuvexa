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
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import kotlin.math.pow

private data class SavingsResult(val futureValue: Double, val totalContributed: Double)

@Composable
fun SavingsCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var initial by remember { mutableStateOf("") }
    var monthly by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var years by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<SavingsResult?>(null) }

    fun calculate() {
        val initialValue = initial.toDoubleOrNull() ?: 0.0
        val monthlyValue = monthly.toDoubleOrNull() ?: 0.0
        val annualRate = rate.toDoubleOrNull() ?: return
        val yearsValue = years.toDoubleOrNull() ?: return
        val n = (yearsValue * 12).toInt()
        if (n <= 0) return
        val monthlyRate = annualRate / 12.0 / 100.0

        val futureValue = if (monthlyRate == 0.0) {
            initialValue + monthlyValue * n
        } else {
            val factor = (1 + monthlyRate).pow(n)
            initialValue * factor + monthlyValue * ((factor - 1) / monthlyRate)
        }
        val totalContributed = initialValue + monthlyValue * n
        result = SavingsResult(futureValue, totalContributed)
        onResult("Savings after $years years = ${formatResultNumber(futureValue)}")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = initial, onValueChange = { initial = it; result = null }, label = stringResource(R.string.savings_initial))
        NuvexaNumberField(value = monthly, onValueChange = { monthly = it; result = null }, label = stringResource(R.string.savings_monthly))
        NuvexaNumberField(value = rate, onValueChange = { rate = it; result = null }, label = stringResource(R.string.label_interest_rate), suffix = "%")
        NuvexaNumberField(value = years, onValueChange = { years = it; result = null }, label = stringResource(R.string.savings_years))
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = rate.isNotBlank() && years.isNotBlank(),
        )
        result?.let { r ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = formatResultNumber(r.futureValue), label = stringResource(R.string.savings_future_value))
                Text(
                    "${stringResource(R.string.savings_total_contributed)}: ${formatResultNumber(r.totalContributed)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
