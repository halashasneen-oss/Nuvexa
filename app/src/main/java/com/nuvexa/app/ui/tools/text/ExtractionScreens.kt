package com.nuvexa.app.ui.tools.text

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
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType

private val numberRegex = Regex("-?\\d+(?:[.,]\\d+)?")
private val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
private val urlRegex = Regex("https?://[^\\s\"'<>]+")

@Composable
private fun ExtractionScreen(modifier: Modifier, inputLabel: String, regex: Regex) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var matches by remember { mutableStateOf<List<String>?>(null) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = input, onValueChange = { input = it; matches = null }, label = inputLabel, minLines = 6)
        PrimaryButton(
            text = stringResource(R.string.action_apply),
            onClick = { matches = regex.findAll(input).map { it.value }.toList() },
            modifier = Modifier.fillMaxWidth(),
            enabled = input.isNotBlank(),
        )

        matches?.let { found ->
            Text(stringResource(R.string.average_count, found.size), style = MaterialTheme.typography.labelLarge)
            if (found.isNotEmpty()) {
                val joined = found.joinToString("\n")
                Text(joined, style = NuvexaExtraType.monospaceBody)
                PrimaryButton(
                    text = stringResource(R.string.action_copy),
                    onClick = { context.copyTextToClipboard(joined) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun ExtractNumbersScreen(modifier: Modifier = Modifier) {
    ExtractionScreen(modifier, stringResource(R.string.tool_text_extract_numbers_name), numberRegex)
}

@Composable
fun ExtractEmailsScreen(modifier: Modifier = Modifier) {
    ExtractionScreen(modifier, stringResource(R.string.tool_text_extract_emails_name), emailRegex)
}

@Composable
fun ExtractUrlsScreen(modifier: Modifier = Modifier) {
    ExtractionScreen(modifier, stringResource(R.string.tool_text_extract_urls_name), urlRegex)
}
