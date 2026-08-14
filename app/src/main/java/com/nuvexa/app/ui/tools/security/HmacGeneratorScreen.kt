package com.nuvexa.app.ui.tools.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
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
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val hmacAlgorithms = listOf("HmacSHA1", "HmacSHA256", "HmacSHA512")

private fun hmac(key: String, message: String, algorithm: String): String {
    val mac = Mac.getInstance(algorithm)
    mac.init(SecretKeySpec(key.toByteArray(Charsets.UTF_8), algorithm))
    return mac.doFinal(message.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
}

@Composable
fun HmacGeneratorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var key by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var algorithm by remember { mutableStateOf("HmacSHA256") }

    val output = remember(key, message, algorithm) {
        if (key.isEmpty() || message.isEmpty()) "" else runCatching { hmac(key, message, algorithm) }.getOrDefault("")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = key, onValueChange = { key = it }, label = stringResource(R.string.hmac_key_label))
        NuvexaTextField(value = message, onValueChange = { message = it }, label = stringResource(R.string.hmac_message_label), minLines = 3)

        Text(stringResource(R.string.hash_algorithm), style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            hmacAlgorithms.forEach { algo ->
                FilterChip(selected = algorithm == algo, onClick = { algorithm = algo }, label = { Text(algo.removePrefix("Hmac")) })
            }
        }

        if (output.isNotEmpty()) {
            ResultCard(
                value = output,
                valueStyle = NuvexaExtraType.monospaceBody,
                onCopy = { context.copyTextToClipboard(output) },
            )
        }
    }
}
