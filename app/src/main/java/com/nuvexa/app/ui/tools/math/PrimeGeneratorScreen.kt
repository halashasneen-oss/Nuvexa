package com.nuvexa.app.ui.tools.math

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType

private fun primesUpTo(limit: Int): List<Int> {
    if (limit < 2) return emptyList()
    val sieve = BooleanArray(limit + 1) { true }
    sieve[0] = false
    if (limit >= 1) sieve[1] = false
    var i = 2
    while (i.toLong() * i <= limit) {
        if (sieve[i]) {
            var multiple = i * i
            while (multiple <= limit) {
                sieve[multiple] = false
                multiple += i
            }
        }
        i++
    }
    return sieve.indices.filter { sieve[it] }
}

@Composable
fun PrimeGeneratorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var limitInput by remember { mutableStateOf("100") }
    var primes by remember { mutableStateOf<List<Int>?>(null) }

    fun generate() {
        val limit = limitInput.toIntOrNull()?.coerceIn(2, 1_000_000) ?: return
        primes = primesUpTo(limit)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = limitInput, onValueChange = { limitInput = it; primes = null }, label = stringResource(R.string.prime_gen_limit), allowDecimal = false)
        PrimaryButton(text = stringResource(R.string.action_generate), onClick = ::generate, modifier = Modifier.fillMaxWidth())

        primes?.let { list ->
            val text = list.joinToString(", ")
            Text(
                stringResource(R.string.average_count, list.size),
                style = MaterialTheme.typography.labelLarge,
            )
            Text(text, style = NuvexaExtraType.monospaceBody)
            PrimaryButton(
                text = stringResource(R.string.action_copy),
                onClick = { context.copyTextToClipboard(text) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
