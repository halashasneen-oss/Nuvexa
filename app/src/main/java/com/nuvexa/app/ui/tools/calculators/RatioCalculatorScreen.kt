package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing

private tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

@Composable
fun RatioCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var first by remember { mutableStateOf("") }
    var second by remember { mutableStateOf("") }
    var simplified by remember { mutableStateOf<Pair<Long, Long>?>(null) }

    fun calculate() {
        val a = first.toLongOrNull()?.takeIf { it != 0L } ?: return
        val b = second.toLongOrNull()?.takeIf { it != 0L } ?: return
        val divisor = gcd(kotlin.math.abs(a), kotlin.math.abs(b)).takeIf { it != 0L } ?: 1L
        simplified = (a / divisor) to (b / divisor)
        onResult("$first:$second simplified to ${a / divisor}:${b / divisor}")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            NuvexaNumberField(value = first, onValueChange = { first = it; simplified = null }, label = stringResource(R.string.ratio_first), allowDecimal = false, modifier = Modifier.weight(1f))
            NuvexaNumberField(value = second, onValueChange = { second = it; simplified = null }, label = stringResource(R.string.ratio_second), allowDecimal = false, modifier = Modifier.weight(1f))
        }

        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = first.isNotBlank() && second.isNotBlank(),
        )

        simplified?.let { (a, b) ->
            ResultCard(value = "$a : $b", label = stringResource(R.string.ratio_simplified))
        }
    }
}
