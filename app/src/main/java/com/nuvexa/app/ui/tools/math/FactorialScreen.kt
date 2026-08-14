package com.nuvexa.app.ui.tools.math

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import java.math.BigInteger

private fun factorial(n: Int): BigInteger {
    var result = BigInteger.ONE
    for (i in 2..n) result = result.multiply(BigInteger.valueOf(i.toLong()))
    return result
}

@Composable
fun FactorialScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val n = input.toIntOrNull()?.takeIf { it in 0..2000 } ?: return
        result = factorial(n).toString()
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = input, onValueChange = { input = it; result = null }, label = stringResource(R.string.factorial_input), allowDecimal = false)
        PrimaryButton(text = stringResource(R.string.action_calculate), onClick = ::calculate, modifier = Modifier.fillMaxWidth(), enabled = input.isNotBlank())
        result?.let { value ->
            ResultCard(
                value = value,
                valueStyle = NuvexaExtraType.monospaceBody,
                onCopy = { context.copyTextToClipboard(value) },
            )
        }
    }
}
