package com.nuvexa.app.ui.tools.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.DatePickerField
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun DateCalculatorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var date by remember { mutableStateOf(LocalDate.now()) }
    var daysToAdd by remember { mutableStateOf("7") }
    val formatter = remember { DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.MEDIUM).withLocale(Locale.getDefault()) }

    val weekNumber = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
    val dayOfYear = date.dayOfYear
    val offsetDays = daysToAdd.toLongOrNull() ?: 0L
    val resultDate = date.plusDays(offsetDays)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        DatePickerField(label = stringResource(R.string.label_start_date), selectedDate = date, onDateSelected = { date = it })

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.l), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.date_calc_week_number), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(weekNumber.toString(), style = MaterialTheme.typography.titleLarge)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.date_calc_day_of_year), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(dayOfYear.toString(), style = MaterialTheme.typography.titleLarge)
            }
        }

        NuvexaNumberField(
            value = daysToAdd,
            onValueChange = { daysToAdd = it },
            label = stringResource(R.string.date_calc_days_amount),
            allowNegative = true,
            allowDecimal = false,
        )

        ResultCard(value = resultDate.format(formatter), label = stringResource(R.string.date_calc_result_date))
    }
}
