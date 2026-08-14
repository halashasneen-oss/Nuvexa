package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import kotlin.math.pow

private data class CompoundResult(val amount: Double, val interest: Double)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompoundInterestScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var principal by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var years by remember { mutableStateOf("") }
    var compoundsPerYear by remember { mutableStateOf(1) }
    var result by remember { mutableStateOf<CompoundResult?>(null) }

    val frequencies = listOf(
        1 to stringResource(R.string.label_compounding_annually),
        2 to stringResource(R.string.label_compounding_semiannually),
        4 to stringResource(R.string.label_compounding_quarterly),
        12 to stringResource(R.string.label_compounding_monthly),
    )

    fun calculate() {
        val p = principal.toDoubleOrNull() ?: return
        val r = rate.toDoubleOrNull() ?: return
        val t = years.toDoubleOrNull() ?: return
        val n = compoundsPerYear.toDouble()
        val amount = p * (1.0 + (r / 100.0) / n).pow(n * t)
        result = CompoundResult(amount, amount - p)
        onResult("Compound interest on $principal = ${formatResultNumber(amount - p)}")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = principal, onValueChange = { principal = it; result = null }, label = stringResource(R.string.label_principal))
        NuvexaNumberField(value = rate, onValueChange = { rate = it; result = null }, label = stringResource(R.string.label_interest_rate), suffix = "%")
        NuvexaNumberField(value = years, onValueChange = { years = it; result = null }, label = stringResource(R.string.label_time_years))

        Text(stringResource(R.string.label_compounding), style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            frequencies.forEach { (value, label) ->
                FilterChip(
                    selected = compoundsPerYear == value,
                    onClick = { compoundsPerYear = value; result = null },
                    label = { Text(label) },
                )
            }
        }

        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = principal.isNotBlank() && rate.isNotBlank() && years.isNotBlank(),
        )

        result?.let { r ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = formatResultNumber(r.interest), label = stringResource(R.string.label_total_interest))
                Text(
                    "${stringResource(R.string.label_final_amount)}: ${formatResultNumber(r.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
