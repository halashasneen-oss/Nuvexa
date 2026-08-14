package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun SplitBillScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var total by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("2") }
    var perPerson by remember { mutableStateOf<BigDecimal?>(null) }

    fun calculate() {
        val totalValue = total.toBigDecimalOrNull() ?: return
        val peopleCount = people.toIntOrNull()?.coerceAtLeast(1) ?: 1
        perPerson = totalValue.divide(BigDecimal(peopleCount), 2, RoundingMode.HALF_UP)
        perPerson?.let { onResult("$total split $peopleCount ways = ${formatResultNumber(it)} each") }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = total, onValueChange = { total = it; perPerson = null }, label = stringResource(R.string.label_total))
        NuvexaNumberField(value = people, onValueChange = { people = it; perPerson = null }, label = stringResource(R.string.tool_calc_split_bill_name), allowDecimal = false)
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = total.isNotBlank(),
        )
        perPerson?.let { ResultCard(value = formatResultNumber(it), label = stringResource(R.string.label_per_person)) }
    }
}
