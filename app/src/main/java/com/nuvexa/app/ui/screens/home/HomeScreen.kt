package com.nuvexa.app.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvexa.app.R
import com.nuvexa.app.core.model.Tool
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.registry.ToolRegistry
import com.nuvexa.app.core.search.SearchEngine
import com.nuvexa.app.ui.components.AppSearchBar
import com.nuvexa.app.ui.components.CategoryCard
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.SectionHeader
import com.nuvexa.app.ui.components.ToolCard
import com.nuvexa.app.ui.components.ToolIconBadge
import com.nuvexa.app.ui.theme.LocalSpacing

@Composable
fun HomeScreen(
    onOpenTool: (String) -> Unit,
    onOpenCategory: (String) -> Unit,
    onSeeAllRecent: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var query by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    val searchResults = androidx.compose.runtime.remember(query) {
        if (query.isBlank()) emptyList() else SearchEngine.search(context, query)
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = spacing.l, vertical = spacing.m),
        verticalArrangement = Arrangement.spacedBy(spacing.xl),
    ) {
        item { HomeHero(onOpenSettings = onOpenSettings) }

        item {
            AppSearchBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = stringResource(R.string.home_search_hint),
            )
        }

        if (query.isNotBlank()) {
            if (searchResults.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Rounded.Search,
                        title = stringResource(R.string.search_no_results_title),
                        body = stringResource(R.string.search_no_results_body),
                    )
                }
            } else {
                items(searchResults, key = { it.id }) { tool ->
                    ToolCard(
                        icon = tool.icon,
                        name = stringResource(tool.nameRes),
                        description = stringResource(tool.descriptionRes),
                        onClick = { onOpenTool(tool.id) },
                    )
                }
            }
        } else {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                    SectionHeader(title = stringResource(R.string.home_quick_actions))
                    LazyRow(
                        contentPadding = PaddingValues(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(spacing.m),
                    ) {
                        items(ToolRegistry.quickActionIds.mapNotNull(ToolRegistry::findById), key = { it.id }) { tool ->
                            QuickActionItem(tool = tool, onClick = { onOpenTool(tool.id) })
                        }
                    }
                }
            }

            if (uiState.favoriteToolIds.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                        SectionHeader(title = stringResource(R.string.home_favorites))
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                            uiState.favoriteToolIds.mapNotNull(ToolRegistry::findById).take(4).forEach { tool ->
                                ToolCard(
                                    icon = tool.icon,
                                    name = stringResource(tool.nameRes),
                                    description = stringResource(tool.descriptionRes),
                                    onClick = { onOpenTool(tool.id) },
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                    SectionHeader(
                        title = stringResource(R.string.home_recent),
                        actionLabel = if (uiState.recentTools.isNotEmpty()) stringResource(R.string.home_recent_see_all) else null,
                        onActionClick = if (uiState.recentTools.isNotEmpty()) onSeeAllRecent else null,
                    )
                    if (uiState.recentTools.isEmpty()) {
                        EmptyState(
                            icon = Icons.Rounded.Search,
                            title = stringResource(R.string.home_empty_recent_title),
                            body = stringResource(R.string.home_empty_recent_body),
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.m)) {
                            items(uiState.recentTools, key = { it.toolId }) { recent ->
                                ToolRegistry.findById(recent.toolId)?.let { tool ->
                                    QuickActionItem(tool = tool, onClick = { onOpenTool(tool.id) })
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.recommendedToolIds.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                        SectionHeader(title = stringResource(R.string.home_recommended))
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                            uiState.recommendedToolIds.mapNotNull(ToolRegistry::findById).forEach { tool ->
                                ToolCard(
                                    icon = tool.icon,
                                    name = stringResource(tool.nameRes),
                                    description = stringResource(tool.descriptionRes),
                                    onClick = { onOpenTool(tool.id) },
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                    SectionHeader(title = stringResource(R.string.home_categories))
                    CategoryGrid(onOpenCategory = onOpenCategory)
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.66f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)),
                ) {
                    Row(
                        modifier = Modifier.padding(spacing.m),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.s),
                    ) {
                        Icon(
                            Icons.Rounded.PrivacyTip,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                        Text(
                            text = stringResource(R.string.home_privacy_banner),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHero(onOpenSettings: () -> Unit) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.m),
    ) {
        ToolIconBadge(icon = Icons.Rounded.GridView, size = 60.dp, iconSize = 30.dp)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium,
            )
            Text(
                text = stringResource(R.string.home_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)),
        ) {
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Rounded.Settings, contentDescription = stringResource(R.string.nav_settings))
            }
        }
    }
}

@Composable
private fun QuickActionItem(tool: Tool, onClick: () -> Unit) {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier
            .width(118.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)),
    ) {
        Column(
            modifier = Modifier.padding(spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            ToolIconBadge(icon = tool.icon, size = 50.dp, iconSize = 25.dp, subtle = true)
            Text(
                text = stringResource(tool.nameRes),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CategoryGrid(onOpenCategory: (String) -> Unit) {
    val spacing = LocalSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(spacing.m)) {
        ToolCategory.entries.toList().chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.m),
            ) {
                pair.forEach { category ->
                    CategoryCard(
                        icon = category.icon,
                        name = stringResource(category.nameRes),
                        toolCount = stringResource(R.string.category_tool_count, ToolRegistry.byCategory(category).size),
                        onClick = { onOpenCategory(category.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
