package com.nuvexa.app.ui.tools.currency

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvexa.app.R
import com.nuvexa.app.core.util.formatResultNumber
import com.nuvexa.app.data.local.entity.CurrencyRateEntity
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.components.SectionHeader
import com.nuvexa.app.ui.theme.LocalSpacing
import java.math.BigDecimal

@Composable
fun CurrencyCalculatorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val viewModel: CurrencyViewModel = hiltViewModel()
    val rates by viewModel.rates.collectAsStateWithLifecycle()

    if (rates.isEmpty()) return

    var fromCode by remember { mutableStateOf("USD") }
    var toCode by remember { mutableStateOf("EUR") }
    var amount by remember { mutableStateOf("1") }

    val fromRate = rates.firstOrNull { it.code == fromCode }?.rate ?: 1.0
    val toRate = rates.firstOrNull { it.code == toCode }?.rate ?: 1.0
    val amountValue = amount.toBigDecimalOrNull()
    val result = amountValue?.let { it / BigDecimal(fromRate) * BigDecimal(toRate) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Text(
            stringResource(R.string.currency_manual_rate_notice),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        NuvexaNumberField(value = amount, onValueChange = { amount = it }, label = stringResource(R.string.action_convert))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            CurrencyDropdown(codes = rates.map { it.code }, selected = fromCode, onSelected = { fromCode = it }, modifier = Modifier.weight(1f))
            IconButton(onClick = { val t = fromCode; fromCode = toCode; toCode = t }) {
                Icon(Icons.Filled.SwapVert, contentDescription = stringResource(R.string.action_convert))
            }
            CurrencyDropdown(codes = rates.map { it.code }, selected = toCode, onSelected = { toCode = it }, modifier = Modifier.weight(1f))
        }

        result?.let { ResultCard(value = "${formatResultNumber(it)} $toCode") }

        SectionHeader(title = stringResource(R.string.currency_edit_rate))
        rates.forEach { rate ->
            RateEditRow(rate = rate, onRateChange = { viewModel.setRate(rate.code, it) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyDropdown(codes: List<String>, selected: String, onSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            codes.forEach { code ->
                DropdownMenuItem(text = { Text(code) }, onClick = { onSelected(code); expanded = false })
            }
        }
    }
}

@Composable
private fun RateEditRow(rate: CurrencyRateEntity, onRateChange: (Double) -> Unit) {
    val spacing = LocalSpacing.current
    var text by remember(rate.code) { mutableStateOf(rate.rate.toString()) }
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
            Text(rate.code, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = text,
                onValueChange = { input ->
                    text = input
                    input.toDoubleOrNull()?.let { onRateChange(it) }
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }
    }
}
