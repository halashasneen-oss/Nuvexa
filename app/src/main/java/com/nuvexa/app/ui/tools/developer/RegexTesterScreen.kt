package com.nuvexa.app.ui.tools.developer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType

@Composable
fun RegexTesterScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var pattern by remember { mutableStateOf("") }
    var caseInsensitive by remember { mutableStateOf(false) }
    var testText by remember { mutableStateOf("") }

    val regexResult = remember(pattern, caseInsensitive) {
        if (pattern.isEmpty()) {
            null
        } else {
            runCatching {
                Regex(pattern, if (caseInsensitive) setOf(RegexOption.IGNORE_CASE) else emptySet())
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        OutlinedTextField(
            value = pattern,
            onValueChange = { pattern = it },
            label = { Text(stringResource(R.string.regex_pattern_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = NuvexaExtraType.monospaceBody,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = caseInsensitive, onCheckedChange = { caseInsensitive = it })
            Text(stringResource(R.string.regex_case_insensitive))
        }

        NuvexaTextField(value = testText, onValueChange = { testText = it }, label = stringResource(R.string.regex_test_text_label), minLines = 6)

        regexResult?.onFailure { e ->
            Text(
                stringResource(R.string.regex_invalid_pattern, e.localizedMessage ?: e.toString()),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        regexResult?.getOrNull()?.let { regex ->
            val matches = regex.findAll(testText).toList()
            Text(stringResource(R.string.regex_match_count, matches.size), style = MaterialTheme.typography.labelLarge)

            if (testText.isNotEmpty()) {
                val annotated = buildAnnotatedString {
                    var cursor = 0
                    matches.forEach { match ->
                        append(testText.substring(cursor, match.range.first))
                        withStyle(SpanStyle(background = Color(0xFFFFF176))) {
                            append(testText.substring(match.range.first, match.range.last + 1))
                        }
                        cursor = match.range.last + 1
                    }
                    if (cursor < testText.length) append(testText.substring(cursor))
                }
                Text(annotated, style = NuvexaExtraType.monospaceBody)
            }
        }
    }
}
