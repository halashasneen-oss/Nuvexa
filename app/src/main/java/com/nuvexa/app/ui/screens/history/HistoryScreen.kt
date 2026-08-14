package com.nuvexa.app.ui.screens.history

import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvexa.app.R
import com.nuvexa.app.core.registry.ToolRegistry
import com.nuvexa.app.ui.components.AppSearchBar
import com.nuvexa.app.ui.components.BannerAdView
import com.nuvexa.app.ui.components.ConfirmDialog
import com.nuvexa.app.ui.components.DangerTextButton
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.theme.LocalSpacing

@Composable
fun HistoryScreen(
    onOpenTool: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val spacing = LocalSpacing.current
    val history by viewModel.history.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var showClearAllDialog by remember { mutableStateOf(false) }

    val filtered = remember(history, query) {
        if (query.isBlank()) {
            history
        } else {
            history.filter { entry ->
                val toolName = ToolRegistry.findById(entry.toolId)?.let { it.id } ?: ""
                entry.summary.contains(query, ignoreCase = true) || toolName.contains(query, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.l, vertical = spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.history_title), style = MaterialTheme.typography.displaySmall)
            if (history.isNotEmpty()) {
                DangerTextButton(text = stringResource(R.string.action_delete_all), onClick = { showClearAllDialog = true })
            }
        }

        if (history.isNotEmpty()) {
            AppSearchBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = stringResource(R.string.history_search_hint),
                modifier = Modifier.padding(horizontal = spacing.l),
            )
        }

        if (history.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.History,
                title = stringResource(R.string.history_empty_title),
                body = stringResource(R.string.history_empty_body),
                modifier = Modifier.padding(top = spacing.xxl),
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(spacing.l),
                verticalArrangement = Arrangement.spacedBy(spacing.s),
            ) {
                items(filtered, key = { it.id }) { entry ->
                    val tool = ToolRegistry.findById(entry.toolId)
                    if (tool != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenTool(tool.id) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        ) {
                            Row(
                                modifier = Modifier.padding(spacing.m),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.m),
                            ) {
                                Icon(tool.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(tool.nameRes), style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        entry.summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        DateUtils.getRelativeTimeSpanString(entry.timestampEpochMillis).toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                IconButton(onClick = { viewModel.delete(entry.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                                }
                            }
                        }
                    }
                }
                item {
                    BannerAdView(modifier = Modifier.padding(top = spacing.m))
                }
            }
        }
    }

    if (showClearAllDialog) {
        ConfirmDialog(
            title = stringResource(R.string.history_clear_all_title),
            body = stringResource(R.string.history_clear_all_body),
            onConfirm = {
                viewModel.clearAll()
                showClearAllDialog = false
            },
            onDismiss = { showClearAllDialog = false },
        )
    }
}
