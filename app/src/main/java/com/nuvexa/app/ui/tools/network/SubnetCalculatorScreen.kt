package com.nuvexa.app.ui.tools.network

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.nuvexa.app.R
import com.nuvexa.app.core.util.parseCidr
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType

@Composable
fun SubnetCalculatorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var input by remember { mutableStateOf("192.168.1.0/24") }
    val info = remember(input) { parseCidr(input) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text(stringResource(R.string.subnet_cidr_input)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = input.isNotBlank() && info == null,
            textStyle = NuvexaExtraType.monospaceBody,
        )

        if (input.isNotBlank() && info == null) {
            Text(stringResource(R.string.subnet_invalid), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        info?.let { subnet ->
            val rows = listOf(
                stringResource(R.string.subnet_network_address) to subnet.networkAddress,
                stringResource(R.string.subnet_broadcast_address) to subnet.broadcastAddress,
                stringResource(R.string.subnet_subnet_mask) to subnet.subnetMask,
                stringResource(R.string.subnet_usable_range) to "${subnet.firstUsable} – ${subnet.lastUsable}",
                stringResource(R.string.subnet_total_hosts) to subnet.usableHostCount.toString(),
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                rows.forEach { (label, value) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, style = NuvexaExtraType.monospaceBody)
                    }
                }
            }
        }
    }
}
