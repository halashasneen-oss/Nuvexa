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
import com.nuvexa.app.ui.components.DatePickerField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun DateDifferenceScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusDays(7)) }
    var days by remember { mutableStateOf<Long?>(null) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        DatePickerField(
            label = stringResource(R.string.label_start_date),
            selectedDate = startDate,
            onDateSelected = { startDate = it; days = null },
        )
        DatePickerField(
            label = stringResource(R.string.label_end_date),
            selectedDate = endDate,
            onDateSelected = { endDate = it; days = null },
        )
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = {
                val d = ChronoUnit.DAYS.between(startDate, endDate)
                days = d
                onResult("$d days")
            },
            modifier = Modifier.fillMaxWidth(),
        )
        days?.let { ResultCard(value = stringResource(R.string.label_days_between, it.toInt())) }
    }
}
