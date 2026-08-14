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

@Composable
fun DiscountCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var price by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var final by remember { mutableStateOf<BigDecimal?>(null) }
    var saved by remember { mutableStateOf<BigDecimal?>(null) }

    fun calculate() {
        val p = price.toBigDecimalOrNull() ?: return
        val d = discount.toBigDecimalOrNull() ?: return
        val finalPrice = p * (BigDecimal(100) - d) / BigDecimal(100)
        final = finalPrice
        saved = p - finalPrice
        onResult("$discount% off $price = ${formatResultNumber(finalPrice)}")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = price, onValueChange = { price = it; final = null }, label = stringResource(R.string.tool_calc_discount_name))
        NuvexaNumberField(value = discount, onValueChange = { discount = it; final = null }, label = "%", suffix = "%")
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = price.isNotBlank() && discount.isNotBlank(),
        )
        final?.let { finalPrice ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = formatResultNumber(finalPrice))
                saved?.let {
                    Text(
                        "− ${formatResultNumber(it)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
