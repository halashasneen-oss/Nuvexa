package com.nuvexa.app.ui.tools.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.theme.LocalSpacing
import kotlin.math.abs
import kotlin.math.max

@Composable
fun TextCompareScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var original by remember { mutableStateOf("") }
    var updated by remember { mutableStateOf("") }
    val stats = remember(original, updated) { compareTexts(original, updated) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.l),
    ) {
        NuvexaTextField(
            value = original,
            onValueChange = { original = it },
            label = stringResource(R.string.text_compare_first),
            minLines = 5,
            maxLines = 10,
        )
        NuvexaTextField(
            value = updated,
            onValueChange = { updated = it },
            label = stringResource(R.string.text_compare_second),
            minLines = 5,
            maxLines = 10,
        )

        if (original.isNotEmpty() || updated.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (stats.identical) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                ),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(spacing.l),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.s),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CompareArrows,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(
                            if (stats.identical) R.string.text_compare_identical else R.string.text_compare_different,
                        ),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = stringResource(
                            R.string.text_compare_summary,
                            stats.changedLines,
                            stats.wordDifference,
                            stats.characterDifference,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    stats.firstChangedLine?.let { line ->
                        Text(
                            text = stringResource(R.string.text_compare_first_change, line),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

private data class TextCompareStats(
    val identical: Boolean,
    val changedLines: Int,
    val wordDifference: Int,
    val characterDifference: Int,
    val firstChangedLine: Int?,
)

private fun compareTexts(first: String, second: String): TextCompareStats {
    val firstLines = if (first.isEmpty()) emptyList() else first.lines()
    val secondLines = if (second.isEmpty()) emptyList() else second.lines()
    val maxLineCount = max(firstLines.size, secondLines.size)
    var changedLines = 0
    var firstChangedLine: Int? = null

    repeat(maxLineCount) { index ->
        if (firstLines.getOrNull(index) != secondLines.getOrNull(index)) {
            changedLines += 1
            if (firstChangedLine == null) firstChangedLine = index + 1
        }
    }

    fun wordCount(value: String): Int = value.trim()
        .takeIf { it.isNotEmpty() }
        ?.split(Regex("\\s+"))
        ?.size ?: 0

    return TextCompareStats(
        identical = first == second,
        changedLines = changedLines,
        wordDifference = abs(wordCount(first) - wordCount(second)),
        characterDifference = abs(first.length - second.length),
        firstChangedLine = firstChangedLine,
    )
}
