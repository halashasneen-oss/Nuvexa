package com.nuvexa.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.nuvexa.app.ui.components.SectionHeader
import com.nuvexa.app.ui.components.ToolCard
import com.nuvexa.app.ui.components.toolVisualStyle
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
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                )
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.nav_settings))
                }
            }
        }

        item { HomeHeroCard() }

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
                    com.nuvexa.app.ui.components.EmptyState(
                        icon = Icons.Filled.Search,
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
                        category = tool.category,
                        onClick = { onOpenTool(tool.id) },
                    )
                }
            }
        } else {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                    SectionHeader(title = stringResource(R.string.home_quick_actions))
                    LazyRow(
                        contentPadding = PaddingValues(end = spacing.s),
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
                                    category = tool.category,
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
                        com.nuvexa.app.ui.components.EmptyState(
                            icon = Icons.Filled.Search,
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
                                    category = tool.category,
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
                    ToolCategory.entries.forEach { category ->
                        CategoryCard(
                            icon = category.icon,
                            name = stringResource(category.nameRes),
                            toolCount = stringResource(R.string.category_tool_count, ToolRegistry.byCategory(category).size),
                            category = category,
                            onClick = { onOpenCategory(category.id) },
                            modifier = Modifier.padding(bottom = spacing.s),
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
                        .padding(spacing.m),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.s),
                ) {
                    Icon(Icons.Filled.PrivacyTip, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = stringResource(R.string.home_privacy_banner),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeroCard() {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.72f),
                        ),
                    ),
                )
                .padding(spacing.l),
            verticalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(
                text = stringResource(R.string.home_hero_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.home_hero_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.86f),
            )
            Text(
                text = stringResource(R.string.home_hero_badge, ToolRegistry.tools.size),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun QuickActionItem(tool: Tool, onClick: () -> Unit) {
    val spacing = LocalSpacing.current
    val visual = toolVisualStyle(tool.category)
    Column(
        modifier = Modifier
            .width(92.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(19.dp))
                .background(visual.container),
            contentAlignment = Alignment.Center,
        ) {
            Icon(tool.icon, contentDescription = null, tint = visual.accent, modifier = Modifier.size(28.dp))
        }
        Text(
            text = stringResource(tool.nameRes),
            style = MaterialTheme.typography.labelMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
