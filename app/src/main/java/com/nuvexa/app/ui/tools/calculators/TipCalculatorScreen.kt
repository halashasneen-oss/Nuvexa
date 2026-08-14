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
import java.math.RoundingMode

private data class TipResult(val tip: BigDecimal, val total: BigDecimal, val perPerson: BigDecimal)

@Composable
fun TipCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var bill by remember { mutableStateOf("") }
    var tipPercent by remember { mutableStateOf("15") }
    var people by remember { mutableStateOf("1") }
    var result by remember { mutableStateOf<TipResult?>(null) }

    fun calculate() {
        val billValue = bill.toBigDecimalOrNull() ?: return
        val tip = tipPercent.toBigDecimalOrNull() ?: return
        val peopleCount = people.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val tipAmount = billValue * tip / BigDecimal(100)
        val total = billValue + tipAmount
        val perPerson = total.divide(BigDecimal(peopleCount), 2, RoundingMode.HALF_UP)
        result = TipResult(tipAmount, total, perPerson)
        onResult("Tip $tipPercent% on $bill = ${formatResultNumber(tipAmount)}")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = bill, onValueChange = { bill = it; result = null }, label = stringResource(R.string.tool_calc_tip_name))
        NuvexaNumberField(value = tipPercent, onValueChange = { tipPercent = it; result = null }, label = "%", suffix = "%")
        NuvexaNumberField(value = people, onValueChange = { people = it; result = null }, label = stringResource(R.string.tool_calc_split_bill_name), allowDecimal = false)

        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = bill.isNotBlank() && tipPercent.isNotBlank(),
        )

        result?.let { r ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = formatResultNumber(r.perPerson), label = stringResource(R.string.label_per_person))
                Text(
                    "${stringResource(R.string.tool_calc_tip_name)}: ${formatResultNumber(r.tip)}  •  ${stringResource(R.string.label_total)}: ${formatResultNumber(r.total)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
