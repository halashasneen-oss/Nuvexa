package com.nuvexa.app.ui.tools.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import kotlin.math.ceil

@Composable
fun TextAnalyzerScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var text by remember { mutableStateOf("") }

    val words = if (text.isBlank()) 0 else text.trim().split(Regex("\\s+")).count { it.isNotBlank() }
    val characters = text.length
    val charactersNoSpaces = text.count { !it.isWhitespace() }
    val sentences = if (text.isBlank()) 0 else text.split(Regex("[.!?]+")).count { it.isNotBlank() }
    val readingMinutes = if (words == 0) 0 else ceil(words / 200.0).toInt().coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(
            value = text,
            onValueChange = { text = it },
            label = stringResource(R.string.tool_text_analyzer_name),
            minLines = 6,
        )

        val stats = listOf(
            stringResource(R.string.tool_text_word_counter_name) to words.toString(),
            stringResource(R.string.tool_text_character_counter_name) to characters.toString(),
            stringResource(R.string.tool_text_character_counter_no_spaces) to charactersNoSpaces.toString(),
            stringResource(R.string.tool_text_sentence_counter_name) to sentences.toString(),
            stringResource(R.string.tool_text_reading_time) to readingMinutes.toString(),
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.s),
            verticalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            stats.forEach { (label, value) ->
                Card(
                    modifier = Modifier.fillMaxWidth(0.46f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.m),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    ) {
                        Text(value, style = NuvexaExtraType.numericMedium, textAlign = TextAlign.Center)
                        Text(
                            label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
