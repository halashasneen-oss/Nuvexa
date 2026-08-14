package com.nuvexa.app.ui.tools.math

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing

private tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
private fun lcm(a: Long, b: Long): Long = if (a == 0L || b == 0L) 0L else kotlin.math.abs(a / gcd(a, b) * b)

@Composable
fun GcdLcmScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var first by remember { mutableStateOf("") }
    var second by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Pair<Long, Long>?>(null) }

    fun calculate() {
        val a = first.toLongOrNull() ?: return
        val b = second.toLongOrNull() ?: return
        result = gcd(kotlin.math.abs(a), kotlin.math.abs(b)) to lcm(a, b)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            NuvexaNumberField(value = first, onValueChange = { first = it; result = null }, label = stringResource(R.string.gcd_first_number), allowDecimal = false, modifier = Modifier.weight(1f))
            NuvexaNumberField(value = second, onValueChange = { second = it; result = null }, label = stringResource(R.string.gcd_second_number), allowDecimal = false, modifier = Modifier.weight(1f))
        }
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            enabled = first.isNotBlank() && second.isNotBlank(),
        )
        result?.let { (gcdValue, lcmValue) ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                ResultCard(value = gcdValue.toString(), label = stringResource(R.string.gcd_result_label))
                Text(
                    "${stringResource(R.string.lcm_result_label)}: $lcmValue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
