package com.nuvexa.app.ui.tools.math

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
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

private fun isPrime(n: Long): Boolean {
    if (n < 2) return false
    if (n < 4) return true
    if (n % 2 == 0L) return false
    var i = 3L
    while (i * i <= n) {
        if (n % i == 0L) return false
        i += 2
    }
    return true
}

@Composable
fun PrimeCheckerScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var input by remember { mutableStateOf("") }
    var checkedValue by remember { mutableStateOf<Pair<Long, Boolean>?>(null) }

    fun calculate() {
        val n = input.toLongOrNull()?.takeIf { it in 0..1_000_000_000_000L } ?: return
        checkedValue = n to isPrime(n)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = input, onValueChange = { input = it; checkedValue = null }, label = stringResource(R.string.prime_input_number), allowDecimal = false)
        PrimaryButton(text = stringResource(R.string.action_calculate), onClick = ::calculate, modifier = Modifier.fillMaxWidth(), enabled = input.isNotBlank())
        checkedValue?.let { (n, prime) ->
            val text = if (prime) stringResource(R.string.prime_is_prime, n.toString()) else stringResource(R.string.prime_is_not_prime, n.toString())
            ResultCard(value = text, valueStyle = MaterialTheme.typography.headlineSmall)
        }
    }
}
