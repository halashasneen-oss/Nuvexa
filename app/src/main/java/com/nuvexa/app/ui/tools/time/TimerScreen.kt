package com.nuvexa.app.ui.tools.time

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import kotlinx.coroutines.delay

@Composable
fun TimerScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var minutesInput by remember { mutableStateOf("5") }
    var secondsInput by remember { mutableStateOf("0") }
    var remainingMs by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingMs > 0) {
            delay(200)
            remainingMs = (remainingMs - 200).coerceAtLeast(0)
        }
        if (isRunning && remainingMs <= 0) {
            isRunning = false
            finished = true
            vibrate(context)
        }
    }

    val totalSeconds = remainingMs / 1000
    val displayText = "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (!isRunning && remainingMs == 0L) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
                NuvexaNumberField(
                    value = minutesInput,
                    onValueChange = { minutesInput = it },
                    label = stringResource(R.string.timer_set_minutes),
                    allowDecimal = false,
                    modifier = Modifier.weight(1f),
                )
                NuvexaNumberField(
                    value = secondsInput,
                    onValueChange = { secondsInput = it },
                    label = stringResource(R.string.timer_set_seconds),
                    allowDecimal = false,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Text(
            text = displayText,
            style = NuvexaExtraType.numericEmphasis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        if (finished) {
            Text(
                stringResource(R.string.timer_finished),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s), modifier = Modifier.fillMaxWidth()) {
            PrimaryButton(
                text = stringResource(if (isRunning) R.string.stopwatch_pause else R.string.timer_start),
                onClick = {
                    if (!isRunning && remainingMs == 0L) {
                        val m = minutesInput.toLongOrNull() ?: 0L
                        val s = secondsInput.toLongOrNull() ?: 0L
                        remainingMs = (m * 60 + s) * 1000
                        finished = false
                    }
                    if (remainingMs > 0) isRunning = !isRunning
                },
                modifier = Modifier.weight(1f),
            )
            SecondaryButton(
                text = stringResource(R.string.stopwatch_reset),
                onClick = { isRunning = false; remainingMs = 0L; finished = false },
                modifier = Modifier.weight(1f),
                enabled = remainingMs > 0 || finished,
            )
        }
    }
}

private fun vibrate(context: android.content.Context) {
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Vibrator::class.java)
            vibrator?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
