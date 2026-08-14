package com.nuvexa.app.ui.screens.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.registry.ToolRegistry
import com.nuvexa.app.ui.components.BannerAdView
import com.nuvexa.app.ui.components.ToolCard
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
        topBar = {
            TopAppBar(
                title = { Text(stringResource(category.nameRes)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(spacing.l),
            verticalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            items(tools, key = { it.id }) { tool ->
                ToolCard(
                    icon = tool.icon,
                    name = stringResource(tool.nameRes),
                    description = stringResource(tool.descriptionRes),
                    onClick = { onOpenTool(tool.id) },
                )
            }
            item {
                BannerAdView(modifier = Modifier.padding(top = spacing.m))
            }
        }
    }
}
