package com.nuvexa.app.ui.tools.network

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.nuvexa.app.core.model.HTTP_STATUS_CODES
import com.nuvexa.app.ui.components.AppSearchBar
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType

@Composable
fun HttpStatusReferenceScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        if (query.isBlank()) {
            HTTP_STATUS_CODES
        } else {
            HTTP_STATUS_CODES.filter {
                it.code.toString().contains(query) || it.reason.contains(query, ignoreCase = true)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.s)) {
        AppSearchBar(query = query, onQueryChange = { query = it }, placeholder = stringResource(R.string.reference_search_hint))
        filtered.forEach { entry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(modifier = Modifier.padding(spacing.m)) {
                    Text("${entry.code} ${entry.reason}", style = NuvexaExtraType.numericMedium)
                    Text(entry.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
