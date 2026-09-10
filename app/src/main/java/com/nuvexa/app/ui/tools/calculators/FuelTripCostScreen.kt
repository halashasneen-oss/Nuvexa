package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import java.text.NumberFormat

private data class TripCostResult(
    val liters: Double,
    val totalCost: Double,
    val perPerson: Double,
)

@Composable
fun FuelTripCostScreen(
    modifier: Modifier = Modifier,
    recordHistory: (String) -> Unit = {},
) {
    val spacing = LocalSpacing.current
    var distance by remember { mutableStateOf("") }
    var consumption by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var passengers by remember { mutableStateOf("1") }
    var result by remember { mutableStateOf<TripCostResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val invalidMessage = stringResource(R.string.trip_invalid_input)
    val numberFormat = remember {
        NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }
    }

    fun calculate() {
        val distanceValue = distance.toDoubleOrNull()
        val consumptionValue = consumption.toDoubleOrNull()
        val priceValue = price.toDoubleOrNull()
        val passengerCount = passengers.toIntOrNull()

        if (distanceValue == null || distanceValue <= 0.0 ||
            consumptionValue == null || consumptionValue <= 0.0 ||
            priceValue == null || priceValue < 0.0 ||
            passengerCount == null || passengerCount <= 0
        ) {
            result = null
            error = invalidMessage
            return
        }

        val liters = distanceValue * consumptionValue / 100.0
        val total = liters * priceValue
        val calculated = TripCostResult(liters, total, total / passengerCount)
        result = calculated
        error = null
        recordHistory("${numberFormat.format(distanceValue)} km → ${numberFormat.format(total)}")
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.l),
    ) {
        NuvexaNumberField(
            value = distance,
            onValueChange = { distance = it; error = null },
            label = stringResource(R.string.trip_distance),
            suffix = "km",
        )
        NuvexaNumberField(
            value = consumption,
            onValueChange = { consumption = it; error = null },
            label = stringResource(R.string.trip_consumption),
            suffix = "L/100 km",
        )
        NuvexaNumberField(
            value = price,
            onValueChange = { price = it; error = null },
            label = stringResource(R.string.trip_fuel_price),
        )
        NuvexaNumberField(
            value = passengers,
            onValueChange = { passengers = it; error = null },
            label = stringResource(R.string.trip_passengers),
            allowDecimal = false,
        )

        PrimaryButton(
            text = stringResource(R.string.trip_calculate),
            onClick = ::calculate,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.LocalGasStation, contentDescription = null) },
        )

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        result?.let { data ->
            ResultCard(
                value = numberFormat.format(data.totalCost),
                label = stringResource(R.string.trip_total_cost),
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)),
            ) {
                Column(
                    modifier = Modifier.padding(spacing.m),
                    verticalArrangement = Arrangement.spacedBy(spacing.s),
                ) {
                    MetricRow(stringResource(R.string.trip_fuel_needed), "${numberFormat.format(data.liters)} L")
                    MetricRow(stringResource(R.string.trip_per_person), numberFormat.format(data.perPerson))
                }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}
