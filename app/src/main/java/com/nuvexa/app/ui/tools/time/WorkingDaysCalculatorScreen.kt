package com.nuvexa.app.ui.tools.time

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
import com.nuvexa.app.ui.components.DatePickerField
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import java.time.DayOfWeek
import java.time.LocalDate

private fun countWorkingDays(start: LocalDate, end: LocalDate): Int {
    val (from, to) = if (start.isAfter(end)) end to start else start to end
    var count = 0
    var current = from
    while (!current.isAfter(to)) {
        if (current.dayOfWeek != DayOfWeek.SATURDAY && current.dayOfWeek != DayOfWeek.SUNDAY) count++
        current = current.plusDays(1)
    }
    return count
}

@Composable
fun WorkingDaysCalculatorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusWeeks(2)) }

    val workingDays = remember(startDate, endDate) { countWorkingDays(startDate, endDate) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        DatePickerField(label = stringResource(R.string.label_start_date), selectedDate = startDate, onDateSelected = { startDate = it })
        DatePickerField(label = stringResource(R.string.label_end_date), selectedDate = endDate, onDateSelected = { endDate = it })
        ResultCard(value = workingDays.toString(), label = stringResource(R.string.working_days_result))
    }
}
