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

private data class MortgageResult(val monthlyPayment: Double, val totalPayment: Double, val totalInterest: Double)

@Composable
fun MortgageCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var homePrice by remember { mutableStateOf("") }
    var downPayment by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var termYears by remember { mutableStateOf("30") }
    var result by remember { mutableStateOf<MortgageResult?>(null) }

    fun calculate() {
        val price = homePrice.toDoubleOrNull() ?: return
        val down = downPayment.toDoubleOrNull() ?: 0.0
        val annualRate = rate.toDoubleOrNull() ?: return
        val years = termYears.toIntOrNull() ?: return
        val principal = (price - down).coerceAtLeast(0.0)
        val n = years * 12
        if (n <= 0) return
        val monthlyRate = annualRate / 12.0 / 100.0
        val payment = if (monthlyRate == 0.0) {
            principal / n
        } else {
            val factor = (1 + monthlyRate).pow(n)
            principal * monthlyRate * factor / (factor - 1)
        }
        val total = payment * n
        result = MortgageResult(payment, total, total - principal)
        onResult("Mortgage on $homePrice = ${formatResultNumber(payment)}/mo")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = homePrice, onValueChange = { homePrice = it; result = null }, label = stringResource(R.string.mortgage_home_price))
        NuvexaNumberField(value = downPayment, onValueChange = { downPayment = it; result = null }, label = stringResource(R.string.mortgage_down_payment))
        NuvexaNumberField(value = rate, onValueChange = { rate = it; result = null }, label = stringResource(R.string.label_interest_rate), suffix = "%")
        NuvexaNumberField(value = termYears, onValueChange = { termYears = it; result = null }, label = stringResource(R.string.mortgage_term_years), allowDecimal = false)
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = homePrice.isNotBlank() && rate.isNotBlank() && termYears.isNotBlank(),
        )
        result?.let { r ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = formatResultNumber(r.monthlyPayment), label = stringResource(R.string.label_monthly_payment))
                Text(
                    "${stringResource(R.string.label_total_payment)}: ${formatResultNumber(r.totalPayment)}  •  ${stringResource(R.string.label_total_interest)}: ${formatResultNumber(r.totalInterest)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
