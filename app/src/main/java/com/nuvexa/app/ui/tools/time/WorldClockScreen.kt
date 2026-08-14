package com.nuvexa.app.ui.tools.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val zones = listOf(
    "America/New_York" to R.string.city_new_york,
    "America/Los_Angeles" to R.string.city_los_angeles,
    "Europe/London" to R.string.city_london,
    "Europe/Paris" to R.string.city_paris,
    "Africa/Cairo" to R.string.city_cairo,
    "Asia/Dubai" to R.string.city_dubai,
    "Asia/Tokyo" to R.string.city_tokyo,
    "Australia/Sydney" to R.string.city_sydney,
)

@Composable
fun WorldClockScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var now by remember { mutableStateOf(ZonedDateTime.now()) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault()) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = ZonedDateTime.now()
            delay(1000)
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.s)) {
        zones.forEach { (zoneId, cityRes) ->
            val zoned = now.withZoneSameInstant(ZoneId.of(zoneId))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.m),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Column {
                        Text(stringResource(cityRes), style = MaterialTheme.typography.titleMedium)
                        Text(
                            zoned.format(dateFormatter),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(zoned.format(timeFormatter), style = NuvexaExtraType.numericMedium)
                }
            }
        }
    }
}
