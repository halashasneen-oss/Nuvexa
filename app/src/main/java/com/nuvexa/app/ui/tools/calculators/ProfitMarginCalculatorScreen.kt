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
import java.math.MathContext

private data class MarginResult(val marginPercent: BigDecimal, val profit: BigDecimal)

@Composable
fun ProfitMarginCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var cost by remember { mutableStateOf("") }
    var revenue by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<MarginResult?>(null) }

    fun calculate() {
        val costValue = cost.toBigDecimalOrNull() ?: return
        val revenueValue = revenue.toBigDecimalOrNull()?.takeIf { it != BigDecimal.ZERO } ?: return
        val profit = revenueValue - costValue
        val margin = profit * BigDecimal(100) / revenueValue
        result = MarginResult(margin, profit)
        onResult("Margin on cost $cost, revenue $revenue = ${formatResultNumber(margin)}%")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = cost, onValueChange = { cost = it; result = null }, label = stringResource(R.string.margin_cost))
        NuvexaNumberField(value = revenue, onValueChange = { revenue = it; result = null }, label = stringResource(R.string.margin_revenue))
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = cost.isNotBlank() && revenue.isNotBlank(),
        )
        result?.let { r ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = "${formatResultNumber(r.marginPercent.round(MathContext(6)))}%", label = stringResource(R.string.margin_result))
                Text(
                    formatResultNumber(r.profit),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
