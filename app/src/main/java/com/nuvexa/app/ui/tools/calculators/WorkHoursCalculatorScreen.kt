package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private fun parseTime(text: String): LocalTime? = runCatching {
    LocalTime.parse(text.trim(), DateTimeFormatter.ofPattern("H:mm"))
}.getOrNull()

@Composable
fun WorkHoursCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var startText by remember { mutableStateOf("9:00") }
    var endText by remember { mutableStateOf("17:30") }
    var breakMinutes by remember { mutableStateOf("30") }
    var result by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val start = parseTime(startText) ?: return
        val end = parseTime(endText) ?: return
        val breakMin = breakMinutes.toLongOrNull() ?: 0L

        var minutesWorked = java.time.Duration.between(start, end).toMinutes()
        if (minutesWorked < 0) minutesWorked += 24 * 60 // overnight shift
        minutesWorked = (minutesWorked - breakMin).coerceAtLeast(0)

        val hours = minutesWorked / 60
        val minutes = minutesWorked % 60
        val text = "%dh %02dm".format(hours, minutes)
        result = text
        onResult("Worked $text ($startText–$endText)")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = startText, onValueChange = { startText = it; result = null }, label = stringResource(R.string.work_hours_start))
        NuvexaTextField(value = endText, onValueChange = { endText = it; result = null }, label = stringResource(R.string.work_hours_end))
        NuvexaNumberField(value = breakMinutes, onValueChange = { breakMinutes = it; result = null }, label = stringResource(R.string.work_hours_break_minutes), allowDecimal = false)
        PrimaryButton(text = stringResource(R.string.action_calculate), onClick = ::calculate, modifier = Modifier.fillMaxWidth())
        result?.let { ResultCard(value = it, label = stringResource(R.string.work_hours_total)) }
    }
}
