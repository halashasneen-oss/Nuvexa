package com.nuvexa.app.ui.screens.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.registry.ToolRegistry
import com.nuvexa.app.core.search.SearchEngine
import com.nuvexa.app.ui.components.AppSearchBar
import com.nuvexa.app.ui.components.CategoryCard
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.ToolCard
import com.nuvexa.app.ui.theme.LocalSpacing

@Composable
fun ToolsScreen(
    onOpenTool: (String) -> Unit,
    onOpenCategory: (String) -> Unit,
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val searchResults = remember(query) {
        if (query.isBlank()) emptyList() else SearchEngine.search(context, query)
    }

    androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.tools_title),
            style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(horizontal = spacing.l, vertical = spacing.m),
        )
        AppSearchBar(
            query = query,
            onQueryChange = { query = it },
            placeholder = stringResource(R.string.tools_search_hint),
            modifier = Modifier.padding(horizontal = spacing.l),
        )

        if (query.isNotBlank()) {
            if (searchResults.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Search,
                    title = stringResource(R.string.search_no_results_title),
                    body = stringResource(R.string.search_no_results_body),
                    modifier = Modifier.padding(top = spacing.xxl),
                )
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    contentPadding = PaddingValues(spacing.l),
                    verticalArrangement = Arrangement.spacedBy(spacing.s),
                ) {
                    items(searchResults, key = { it.id }) { tool ->
                        ToolCard(
                            icon = tool.icon,
                            name = stringResource(tool.nameRes),
                            description = stringResource(tool.descriptionRes),
                            onClick = { onOpenTool(tool.id) },
                        )
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(spacing.l),
                horizontalArrangement = Arrangement.spacedBy(spacing.m),
                verticalArrangement = Arrangement.spacedBy(spacing.m),
            ) {
                items(ToolCategory.entries.toList(), key = { it.id }) { category ->
                    CategoryCard(
                        icon = category.icon,
                        name = stringResource(category.nameRes),
                        toolCount = stringResource(R.string.category_tool_count, ToolRegistry.byCategory(category).size),
                        onClick = { onOpenCategory(category.id) },
                    )
                }
            }
        }
    }
}
