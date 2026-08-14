package com.nuvexa.app.ui.screens.favorites

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.ToolCard
import com.nuvexa.app.ui.theme.LocalSpacing

@Composable
fun FavoritesScreen(
    onOpenTool: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val spacing = LocalSpacing.current
    val favorites by viewModel.favoriteTools.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.favorites_title),
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.l, vertical = spacing.m),
        )

        if (favorites.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Star,
                title = stringResource(R.string.favorites_empty_title),
                body = stringResource(R.string.favorites_empty_body),
                modifier = Modifier.padding(top = spacing.xxl),
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(spacing.l),
                verticalArrangement = Arrangement.spacedBy(spacing.s),
            ) {
                items(favorites, key = { it.id }) { tool ->
                    val index = favorites.indexOf(tool)
                    ToolCard(
                        icon = tool.icon,
                        name = stringResource(tool.nameRes),
                        description = stringResource(tool.descriptionRes),
                        onClick = { onOpenTool(tool.id) },
                        trailing = {
                            Row {
                                IconButton(onClick = { viewModel.moveUp(tool.id) }, enabled = index > 0) {
                                    Icon(Icons.Filled.ArrowUpward, contentDescription = null)
                                }
                                IconButton(onClick = { viewModel.moveDown(tool.id) }, enabled = index < favorites.lastIndex) {
                                    Icon(Icons.Filled.ArrowDownward, contentDescription = null)
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
