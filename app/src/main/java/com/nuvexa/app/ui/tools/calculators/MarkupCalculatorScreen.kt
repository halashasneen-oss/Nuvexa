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

@Composable
fun MarkupCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var cost by remember { mutableStateOf("") }
    var markupPercent by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf<BigDecimal?>(null) }

    fun calculate() {
        val costValue = cost.toBigDecimalOrNull() ?: return
        val markup = markupPercent.toBigDecimalOrNull() ?: return
        val price = costValue * (BigDecimal(100) + markup) / BigDecimal(100)
        sellingPrice = price
        onResult("Cost $cost + $markupPercent% markup = ${formatResultNumber(price)}")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = cost, onValueChange = { cost = it; sellingPrice = null }, label = stringResource(R.string.markup_cost))
        NuvexaNumberField(value = markupPercent, onValueChange = { markupPercent = it; sellingPrice = null }, label = stringResource(R.string.markup_percent), suffix = "%")
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = cost.isNotBlank() && markupPercent.isNotBlank(),
        )
        sellingPrice?.let { ResultCard(value = formatResultNumber(it)) }
    }
}
