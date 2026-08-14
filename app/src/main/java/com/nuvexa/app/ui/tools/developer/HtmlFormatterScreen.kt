package com.nuvexa.app.ui.tools.developer

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
import com.nuvexa.app.core.util.formatHtml
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType

@Composable
fun HtmlFormatterScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = input, onValueChange = { input = it }, label = stringResource(R.string.tool_dev_html_formatter_name), minLines = 6)
        PrimaryButton(
            text = stringResource(R.string.action_format),
            onClick = { output = formatHtml(input) },
            modifier = Modifier.fillMaxWidth(),
            enabled = input.isNotBlank(),
        )

        if (output.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                Text(stringResource(R.string.output_label), style = MaterialTheme.typography.labelLarge)
                Text(output, style = NuvexaExtraType.monospaceBody)
                PrimaryButton(
                    text = stringResource(R.string.action_copy),
                    onClick = { context.copyTextToClipboard(output) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
