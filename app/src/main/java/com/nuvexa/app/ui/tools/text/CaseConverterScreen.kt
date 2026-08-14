package com.nuvexa.app.ui.tools.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import java.util.Locale

private enum class CaseMode { UPPER, LOWER, TITLE, SENTENCE }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaseConverterScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }

    fun applyCase(mode: CaseMode) {
        text = when (mode) {
            CaseMode.UPPER -> text.uppercase(Locale.getDefault())
            CaseMode.LOWER -> text.lowercase(Locale.getDefault())
            CaseMode.TITLE -> text.lowercase(Locale.getDefault()).split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { it.titlecase(Locale.getDefault()) }
            }
            CaseMode.SENTENCE -> text.lowercase(Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = text, onValueChange = { text = it }, label = stringResource(R.string.tool_text_case_converter_name), minLines = 6)

        FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            SecondaryButton(text = stringResource(R.string.case_uppercase), onClick = { applyCase(CaseMode.UPPER) })
            SecondaryButton(text = stringResource(R.string.case_lowercase), onClick = { applyCase(CaseMode.LOWER) })
            SecondaryButton(text = stringResource(R.string.case_title), onClick = { applyCase(CaseMode.TITLE) })
            SecondaryButton(text = stringResource(R.string.case_sentence), onClick = { applyCase(CaseMode.SENTENCE) })
        }

        PrimaryButton(
            text = stringResource(R.string.action_copy),
            onClick = { context.copyTextToClipboard(text) },
            modifier = Modifier.fillMaxWidth(),
            enabled = text.isNotBlank(),
        )
    }
}
