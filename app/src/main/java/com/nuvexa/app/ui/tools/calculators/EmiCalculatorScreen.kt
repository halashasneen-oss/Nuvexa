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

private data class EmiResult(val emi: Double, val totalPayment: Double, val totalInterest: Double)

@Composable
fun EmiCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var loanAmount by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var tenureMonths by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<EmiResult?>(null) }

    fun calculate() {
        val p = loanAmount.toDoubleOrNull() ?: return
        val annualRate = rate.toDoubleOrNull() ?: return
        val n = tenureMonths.toIntOrNull() ?: return
        if (n <= 0) return
        val monthlyRate = annualRate / 12.0 / 100.0
        val emi = if (monthlyRate == 0.0) {
            p / n
        } else {
            val factor = (1 + monthlyRate).pow(n)
            p * monthlyRate * factor / (factor - 1)
        }
        val total = emi * n
        result = EmiResult(emi, total, total - p)
        onResult("EMI on $loanAmount = ${formatResultNumber(emi)}")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = loanAmount, onValueChange = { loanAmount = it; result = null }, label = stringResource(R.string.label_loan_amount))
        NuvexaNumberField(value = rate, onValueChange = { rate = it; result = null }, label = stringResource(R.string.label_interest_rate), suffix = "%")
        NuvexaNumberField(value = tenureMonths, onValueChange = { tenureMonths = it; result = null }, label = stringResource(R.string.label_tenure_months), allowDecimal = false)
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = loanAmount.isNotBlank() && rate.isNotBlank() && tenureMonths.isNotBlank(),
        )
        result?.let { r ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = formatResultNumber(r.emi), label = stringResource(R.string.label_monthly_payment))
                Text(
                    "${stringResource(R.string.label_total_payment)}: ${formatResultNumber(r.totalPayment)}  •  ${stringResource(R.string.label_total_interest)}: ${formatResultNumber(r.totalInterest)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
