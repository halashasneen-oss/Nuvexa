package com.nuvexa.app.ui.tools.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nuvexa.app.R
import com.nuvexa.app.core.model.UnitCategory
import com.nuvexa.app.core.model.UnitDefinition
import com.nuvexa.app.core.util.formatResultNumber
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UnitConverterScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var category by remember { mutableStateOf(UnitCategory.LENGTH) }
    var fromUnitIndex by remember { mutableStateOf(0) }
    var toUnitIndex by remember { mutableStateOf(1) }
    var input by remember { mutableStateOf("1") }

    val units = category.units
    val fromUnit = units.getOrElse(fromUnitIndex) { units[0] }
    val toUnit = units.getOrElse(toUnitIndex) { units.getOrElse(1) { units[0] } }

    val inputValue = input.toDoubleOrNull()
    val result = inputValue?.let { toUnit.fromBase(fromUnit.toBase(it)) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            UnitCategory.entries.forEach { entry ->
                FilterChip(
                    selected = category == entry,
                    onClick = {
                        category = entry
                        fromUnitIndex = 0
                        toUnitIndex = if (entry.units.size > 1) 1 else 0
                    },
                    label = { Text(stringResource(entry.labelRes)) },
                )
            }
        }

        NuvexaNumberField(value = input, onValueChange = { input = it }, label = stringResource(category.labelRes), allowNegative = true)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            UnitDropdown(
                modifier = Modifier.weight(1f),
                units = units,
                selectedIndex = fromUnitIndex,
                onSelected = { fromUnitIndex = it },
            )
            IconButton(onClick = {
                val temp = fromUnitIndex
                fromUnitIndex = toUnitIndex
                toUnitIndex = temp
            }) {
                Icon(Icons.Filled.SwapVert, contentDescription = stringResource(R.string.action_convert))
            }
            UnitDropdown(
                modifier = Modifier.weight(1f),
                units = units,
                selectedIndex = toUnitIndex,
                onSelected = { toUnitIndex = it },
            )
        }

        result?.let {
            ResultCard(value = "${formatResultNumber(it)} ${toUnit.symbol}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(
    units: List<UnitDefinition>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = units.getOrElse(selectedIndex) { units.first() }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = selected.symbol,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            units.forEachIndexed { index, unit ->
                DropdownMenuItem(text = { Text(unit.symbol) }, onClick = { onSelected(index); expanded = false })
            }
        }
    }
}
