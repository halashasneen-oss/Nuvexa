package com.nuvexa.app.ui.tools.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
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
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class UnixMode { TO_DATE, TO_TIMESTAMP }

@Composable
fun UnixTimestampScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var mode by remember { mutableStateOf(UnixMode.TO_DATE) }
    var timestampInput by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var hour by remember { mutableStateOf("12") }
    var minute by remember { mutableStateOf("0") }

    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            FilterChip(selected = mode == UnixMode.TO_DATE, onClick = { mode = UnixMode.TO_DATE }, label = { Text(stringResource(R.string.unix_date_label)) })
            FilterChip(selected = mode == UnixMode.TO_TIMESTAMP, onClick = { mode = UnixMode.TO_TIMESTAMP }, label = { Text(stringResource(R.string.unix_timestamp_label)) })
        }

        if (mode == UnixMode.TO_DATE) {
            NuvexaNumberField(
                value = timestampInput,
                onValueChange = { timestampInput = it },
                label = stringResource(R.string.unix_timestamp_label),
                allowNegative = true,
                allowDecimal = false,
            )
            SecondaryButton(
                text = stringResource(R.string.unix_now),
                onClick = { timestampInput = (System.currentTimeMillis() / 1000).toString() },
            )
            val seconds = timestampInput.toLongOrNull()
            if (seconds != null) {
                val dateTime = LocalDateTime.ofInstant(java.time.Instant.ofEpochSecond(seconds), ZoneId.systemDefault())
                ResultCard(value = dateTime.format(formatter))
            }
        } else {
            DatePickerField(label = stringResource(R.string.unix_date_label), selectedDate = date, onDateSelected = { date = it })
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
                NuvexaNumberField(value = hour, onValueChange = { hour = it }, label = "HH", allowDecimal = false, modifier = Modifier.weight(1f))
                NuvexaNumberField(value = minute, onValueChange = { minute = it }, label = "mm", allowDecimal = false, modifier = Modifier.weight(1f))
            }
            val h = hour.toIntOrNull()?.coerceIn(0, 23) ?: 0
            val m = minute.toIntOrNull()?.coerceIn(0, 59) ?: 0
            val epoch = LocalDateTime.of(date, java.time.LocalTime.of(h, m)).atZone(ZoneId.systemDefault()).toEpochSecond()
            ResultCard(value = epoch.toString())
        }
    }
}
