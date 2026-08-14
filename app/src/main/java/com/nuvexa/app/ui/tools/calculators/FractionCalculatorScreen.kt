package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import java.math.BigDecimal
import java.math.MathContext

private enum class FractionOp(val symbol: String) { ADD("+"), SUBTRACT("−"), MULTIPLY("×"), DIVIDE("÷") }

private tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

@Composable
fun FractionCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var num1 by remember { mutableStateOf("1") }
    var den1 by remember { mutableStateOf("2") }
    var num2 by remember { mutableStateOf("1") }
    var den2 by remember { mutableStateOf("3") }
    var op by remember { mutableStateOf(FractionOp.ADD) }
    var result by remember { mutableStateOf<Pair<Long, Long>?>(null) }

    fun calculate() {
        val n1 = num1.toLongOrNull() ?: return
        val d1 = den1.toLongOrNull()?.takeIf { it != 0L } ?: return
        val n2 = num2.toLongOrNull() ?: return
        val d2 = den2.toLongOrNull()?.takeIf { it != 0L } ?: return

        var resultNum: Long
        var resultDen: Long
        when (op) {
            FractionOp.ADD -> { resultNum = n1 * d2 + n2 * d1; resultDen = d1 * d2 }
            FractionOp.SUBTRACT -> { resultNum = n1 * d2 - n2 * d1; resultDen = d1 * d2 }
            FractionOp.MULTIPLY -> { resultNum = n1 * n2; resultDen = d1 * d2 }
            FractionOp.DIVIDE -> {
                if (n2 == 0L) return
                resultNum = n1 * d2
                resultDen = d1 * n2
            }
        }
        if (resultDen < 0) { resultNum = -resultNum; resultDen = -resultDen }
        val divisor = gcd(kotlin.math.abs(resultNum), resultDen).takeIf { it != 0L } ?: 1L
        resultNum /= divisor
        resultDen /= divisor
        result = resultNum to resultDen
        onResult("$num1/$den1 ${op.symbol} $num2/$den2 = $resultNum/$resultDen")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            NuvexaNumberField(value = num1, onValueChange = { num1 = it; result = null }, label = stringResource(R.string.fraction_numerator), allowNegative = true, modifier = Modifier.weight(1f))
            NuvexaNumberField(value = den1, onValueChange = { den1 = it; result = null }, label = stringResource(R.string.fraction_denominator), allowNegative = true, modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            FractionOp.entries.forEach { operation ->
                FilterChip(selected = op == operation, onClick = { op = operation; result = null }, label = { Text(operation.symbol) })
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            NuvexaNumberField(value = num2, onValueChange = { num2 = it; result = null }, label = stringResource(R.string.fraction_numerator), allowNegative = true, modifier = Modifier.weight(1f))
            NuvexaNumberField(value = den2, onValueChange = { den2 = it; result = null }, label = stringResource(R.string.fraction_denominator), allowNegative = true, modifier = Modifier.weight(1f))
        }

        PrimaryButton(text = stringResource(R.string.action_calculate), onClick = ::calculate, modifier = Modifier.fillMaxWidth())

        result?.let { (n, d) ->
            val decimal = formatResultNumber(BigDecimal(n).divide(BigDecimal(d), MathContext.DECIMAL64))
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = "$n/$d")
                Text("= $decimal", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
