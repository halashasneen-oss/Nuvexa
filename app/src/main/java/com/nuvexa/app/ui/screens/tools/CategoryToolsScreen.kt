package com.nuvexa.app.ui.screens.tools

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.registry.ToolRegistry
import com.nuvexa.app.ui.components.BannerAdView
import com.nuvexa.app.ui.components.ToolIconBadge
import com.nuvexa.app.ui.components.ToolTile
import com.nuvexa.app.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryToolsScreen(
    categoryId: String,
    onOpenTool: (String) -> Unit,
    onBack: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val category = ToolCategory.entries.firstOrNull { it.id == categoryId } ?: return
    val tools = ToolRegistry.byCategory(category)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Surface(
                        modifier = Modifier.padding(start = 8.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)),
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.action_back),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 148.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(spacing.l),
            horizontalArrangement = Arrangement.spacedBy(spacing.m),
            verticalArrangement = Arrangement.spacedBy(spacing.m),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)),
                ) {
                    Row(
                        modifier = Modifier.padding(spacing.l),
                        horizontalArrangement = Arrangement.spacedBy(spacing.m),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ToolIconBadge(icon = category.icon, size = 60.dp, iconSize = 30.dp)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = stringResource(category.nameRes),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Text(
                                text = stringResource(category.descriptionRes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            items(tools, key = { it.id }) { tool ->
                ToolTile(
                    icon = tool.icon,
                    name = stringResource(tool.nameRes),
                    onClick = { onOpenTool(tool.id) },
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                // Existing ad placement intentionally preserved.
                BannerAdView(modifier = Modifier.fillMaxWidth().padding(top = spacing.s))
            }
        }
    }
}
