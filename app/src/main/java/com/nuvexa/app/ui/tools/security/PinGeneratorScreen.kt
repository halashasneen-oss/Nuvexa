package com.nuvexa.app.ui.tools.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import java.security.SecureRandom
import com.nuvexa.app.ui.theme.LocalSpacing

@Composable
fun PinGeneratorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var digits by remember { mutableStateOf(4f) }
    var pin by remember { mutableStateOf("") }

    fun generate() {
        val random = SecureRandom()
        pin = (1..digits.toInt()).map { random.nextInt(10) }.joinToString("")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Text(stringResource(R.string.pin_length, digits.toInt()), style = MaterialTheme.typography.labelLarge)
        Slider(value = digits, onValueChange = { digits = it }, valueRange = 3f..12f, steps = 8)
        PrimaryButton(text = stringResource(R.string.action_generate), onClick = ::generate, modifier = Modifier.fillMaxWidth())
        if (pin.isNotEmpty()) {
            ResultCard(value = pin, onCopy = { context.copyTextToClipboard(pin) })
        }
    }
}
