package com.nuvexa.app.ui.tools.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import java.security.MessageDigest

private val algorithms = listOf("MD5", "SHA-1", "SHA-256", "SHA-512")

private fun hash(text: String, algorithm: String): String {
    val digest = MessageDigest.getInstance(algorithm).digest(text.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { "%02x".format(it) }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HashGeneratorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var algorithm by remember { mutableStateOf("SHA-256") }

    val output = remember(input, algorithm) { if (input.isEmpty()) "" else hash(input, algorithm) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = input, onValueChange = { input = it }, label = stringResource(R.string.input_label), minLines = 3)

        Text(stringResource(R.string.hash_algorithm), style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            algorithms.forEach { algo ->
                FilterChip(selected = algorithm == algo, onClick = { algorithm = algo }, label = { Text(algo) })
            }
        }

        if (output.isNotEmpty()) {
            ResultCard(
                value = output,
                label = algorithm,
                valueStyle = NuvexaExtraType.monospaceBody,
                onCopy = { context.copyTextToClipboard(output) },
            )
        }
    }
}
