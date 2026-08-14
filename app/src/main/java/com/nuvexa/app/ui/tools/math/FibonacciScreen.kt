package com.nuvexa.app.ui.tools.math

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import java.math.BigInteger

private fun fibonacci(count: Int): List<BigInteger> {
    if (count <= 0) return emptyList()
    val sequence = mutableListOf(BigInteger.ZERO)
    if (count == 1) return sequence
    sequence.add(BigInteger.ONE)
    while (sequence.size < count) {
        sequence.add(sequence[sequence.size - 1] + sequence[sequence.size - 2])
    }
    return sequence
}

@Composable
fun FibonacciScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var countInput by remember { mutableStateOf("15") }
    var sequence by remember { mutableStateOf<List<BigInteger>?>(null) }

    fun generate() {
        val count = countInput.toIntOrNull()?.coerceIn(1, 500) ?: return
        sequence = fibonacci(count)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = countInput, onValueChange = { countInput = it; sequence = null }, label = stringResource(R.string.fibonacci_count), allowDecimal = false)
        PrimaryButton(text = stringResource(R.string.action_generate), onClick = ::generate, modifier = Modifier.fillMaxWidth())

        sequence?.let { list ->
            val text = list.joinToString(", ")
            Text(text, style = NuvexaExtraType.monospaceBody)
            PrimaryButton(
                text = stringResource(R.string.action_copy),
                onClick = { context.copyTextToClipboard(text) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
