package com.nuvexa.app.ui.tools.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import kotlinx.coroutines.delay

private fun formatElapsed(ms: Long): String {
    val minutes = (ms / 60000) % 60
    val seconds = (ms / 1000) % 60
    val tenths = (ms % 1000) / 100
    return "%02d:%02d.%d".format(minutes, seconds, tenths)
}

@Composable
fun StopwatchScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var isRunning by remember { mutableStateOf(false) }
    var elapsedMs by remember { mutableStateOf(0L) }
    var laps by remember { mutableStateOf(listOf<Long>()) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            val startReference = System.currentTimeMillis() - elapsedMs
            while (isRunning) {
                elapsedMs = System.currentTimeMillis() - startReference
                delay(33)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Text(
            text = formatElapsed(elapsedMs),
            style = NuvexaExtraType.numericEmphasis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s), modifier = Modifier.fillMaxWidth()) {
            PrimaryButton(
                text = stringResource(if (isRunning) R.string.stopwatch_pause else R.string.stopwatch_start),
                onClick = { isRunning = !isRunning },
                modifier = Modifier.weight(1f),
            )
            SecondaryButton(
                text = stringResource(R.string.stopwatch_lap),
                onClick = { laps = laps + elapsedMs },
                modifier = Modifier.weight(1f),
                enabled = isRunning,
            )
            SecondaryButton(
                text = stringResource(R.string.stopwatch_reset),
                onClick = { isRunning = false; elapsedMs = 0L; laps = emptyList() },
                modifier = Modifier.weight(1f),
                enabled = !isRunning && elapsedMs > 0,
            )
        }

        if (laps.isNotEmpty()) {
            HorizontalDivider()
            LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                items(laps.reversed().withIndex().toList()) { (index, lap) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("${stringResource(R.string.stopwatch_lap)} ${laps.size - index}", style = MaterialTheme.typography.bodyMedium)
                        Text(formatElapsed(lap), style = NuvexaExtraType.monospaceBody)
                    }
                }
            }
        }
    }
}
