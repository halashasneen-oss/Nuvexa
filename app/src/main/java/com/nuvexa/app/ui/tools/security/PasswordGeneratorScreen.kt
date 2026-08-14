package com.nuvexa.app.ui.tools.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import java.security.SecureRandom

private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
private const val DIGITS = "0123456789"
private const val SYMBOLS = "!@#\$%^&*()-_=+[]{}"

@Composable
fun PasswordGeneratorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var length by remember { mutableStateOf(16f) }
    var useUpper by remember { mutableStateOf(true) }
    var useLower by remember { mutableStateOf(true) }
    var useDigits by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }

    fun generate() {
        val pool = buildString {
            if (useUpper) append(UPPER)
            if (useLower) append(LOWER)
            if (useDigits) append(DIGITS)
            if (useSymbols) append(SYMBOLS)
        }.ifEmpty { LOWER }
        val random = SecureRandom()
        password = (1..length.toInt()).map { pool[random.nextInt(pool.length)] }.joinToString("")
    }

    val classesUsed = listOf(useUpper, useLower, useDigits, useSymbols).count { it }
    val strengthLabel = when {
        password.isEmpty() -> null
        length >= 14 && classesUsed >= 3 -> stringResource(R.string.password_strength_strong)
        length >= 10 && classesUsed >= 2 -> stringResource(R.string.password_strength_fair)
        else -> stringResource(R.string.password_strength_weak)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Text(stringResource(R.string.password_length, length.toInt()), style = MaterialTheme.typography.labelLarge)
        Slider(value = length, onValueChange = { length = it }, valueRange = 8f..64f, steps = 55)

        listOf(
            Triple(R.string.password_include_uppercase, useUpper) { v: Boolean -> useUpper = v },
            Triple(R.string.password_include_lowercase, useLower) { v: Boolean -> useLower = v },
            Triple(R.string.password_include_numbers, useDigits) { v: Boolean -> useDigits = v },
            Triple(R.string.password_include_symbols, useSymbols) { v: Boolean -> useSymbols = v },
        ).forEach { (labelRes, checked, onChange) ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = checked, onCheckedChange = onChange)
                Text(stringResource(labelRes))
            }
        }

        PrimaryButton(text = stringResource(R.string.action_generate), onClick = ::generate, modifier = Modifier.fillMaxWidth())

        if (password.isNotEmpty()) {
            ResultCard(
                value = password,
                label = strengthLabel,
                valueStyle = NuvexaExtraType.monospaceBody,
                onCopy = { context.copyTextToClipboard(password) },
            )
        }
    }
}
