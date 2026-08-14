package com.nuvexa.app.ui.tools.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import org.json.JSONArray
import org.json.JSONObject

private fun parseJson(input: String): Any = if (input.trim().startsWith("[")) JSONArray(input) else JSONObject(input)

@Composable
fun JsonFormatterScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val invalidJsonTemplate = stringResource(R.string.json_invalid)

    fun run(pretty: Boolean) {
        val parsed = runCatching { parseJson(input) }
        parsed.onSuccess { value ->
            output = when (value) {
                is JSONArray -> if (pretty) value.toString(2) else value.toString()
                is JSONObject -> if (pretty) value.toString(2) else value.toString()
                else -> ""
            }
            errorMessage = null
        }.onFailure { e ->
            output = ""
            errorMessage = invalidJsonTemplate.format(e.localizedMessage ?: e.toString())
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = input, onValueChange = { input = it }, label = stringResource(R.string.tool_text_json_formatter_name), minLines = 6)

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            PrimaryButton(text = stringResource(R.string.action_format), onClick = { run(pretty = true) }, enabled = input.isNotBlank())
            SecondaryButton(text = stringResource(R.string.action_minify), onClick = { run(pretty = false) }, enabled = input.isNotBlank())
        }

        errorMessage?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }

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
