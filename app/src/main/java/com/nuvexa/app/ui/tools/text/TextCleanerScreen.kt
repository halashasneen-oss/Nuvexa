package com.nuvexa.app.ui.tools.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing

@Composable
fun TextCleanerScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = text, onValueChange = { text = it }, label = stringResource(R.string.tool_text_cleaner_name), minLines = 8)

        FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            SecondaryButton(
                text = stringResource(R.string.clean_trim_spaces),
                onClick = { text = text.split("\n").joinToString("\n") { it.trim().replace(Regex(" +"), " ") } },
            )
            SecondaryButton(
                text = stringResource(R.string.clean_remove_empty_lines),
                onClick = { text = text.lines().filter { it.isNotBlank() }.joinToString("\n") },
            )
            SecondaryButton(
                text = stringResource(R.string.clean_remove_duplicate_lines),
                onClick = { text = text.lines().distinct().joinToString("\n") },
            )
            SecondaryButton(
                text = stringResource(R.string.clean_sort_lines),
                onClick = { text = text.lines().sorted().joinToString("\n") },
            )
        }

        PrimaryButton(
            text = stringResource(R.string.action_copy),
            onClick = { context.copyTextToClipboard(text) },
            modifier = Modifier.fillMaxWidth(),
            enabled = text.isNotBlank(),
        )
    }
}
