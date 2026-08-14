package com.nuvexa.app.ui.tools.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
import com.nuvexa.app.core.util.PASSPHRASE_WORD_LIST
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import java.security.SecureRandom

private val separators = listOf("-", "_", " ", ".")

@Composable
fun PassphraseGeneratorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var wordCount by remember { mutableStateOf(4f) }
    var separator by remember { mutableStateOf("-") }
    var passphrase by remember { mutableStateOf("") }

    fun generate() {
        val random = SecureRandom()
        passphrase = (1..wordCount.toInt())
            .map { PASSPHRASE_WORD_LIST[random.nextInt(PASSPHRASE_WORD_LIST.size)] }
            .joinToString(separator)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Text(stringResource(R.string.passphrase_word_count) + ": ${wordCount.toInt()}", style = MaterialTheme.typography.labelLarge)
        Slider(value = wordCount, onValueChange = { wordCount = it }, valueRange = 3f..8f, steps = 4)

        Text(stringResource(R.string.passphrase_separator), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            separators.forEach { sep ->
                FilterChip(selected = separator == sep, onClick = { separator = sep }, label = { Text(if (sep == " ") "␣" else sep) })
            }
        }

        PrimaryButton(text = stringResource(R.string.action_generate), onClick = ::generate, modifier = Modifier.fillMaxWidth())

        if (passphrase.isNotEmpty()) {
            ResultCard(
                value = passphrase,
                valueStyle = NuvexaExtraType.monospaceBody,
                onCopy = { context.copyTextToClipboard(passphrase) },
            )
        }
    }
}
