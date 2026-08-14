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
import java.math.BigDecimal

private data class InterestResult(val interest: BigDecimal, val total: BigDecimal)

@Composable
fun SimpleInterestScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var principal by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var years by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<InterestResult?>(null) }

    fun calculate() {
        val p = principal.toBigDecimalOrNull() ?: return
        val r = rate.toBigDecimalOrNull() ?: return
        val t = years.toBigDecimalOrNull() ?: return
        val interest = p * r * t / BigDecimal(100)
        result = InterestResult(interest, p + interest)
        onResult("Simple interest on $principal = ${formatResultNumber(interest)}")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = principal, onValueChange = { principal = it; result = null }, label = stringResource(R.string.label_principal))
        NuvexaNumberField(value = rate, onValueChange = { rate = it; result = null }, label = stringResource(R.string.label_interest_rate), suffix = "%")
        NuvexaNumberField(value = years, onValueChange = { years = it; result = null }, label = stringResource(R.string.label_time_years))
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = principal.isNotBlank() && rate.isNotBlank() && years.isNotBlank(),
        )
        result?.let { r ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = formatResultNumber(r.interest), label = stringResource(R.string.label_interest))
                Text(
                    "${stringResource(R.string.label_final_amount)}: ${formatResultNumber(r.total)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
