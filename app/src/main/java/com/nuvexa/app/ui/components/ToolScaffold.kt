package com.nuvexa.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.model.Tool
import com.nuvexa.app.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolScaffold(
    tool: Tool,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    val spacing = LocalSpacing.current
    val scheme = MaterialTheme.colorScheme


    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = scheme.onBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(tool.nameRes),
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    Surface(
                        modifier = Modifier.padding(start = 8.dp),
                        shape = CircleShape,
                        color = scheme.surface.copy(alpha = 0.94f),
                        contentColor = scheme.onSurface,
                        border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.24f)),
                        shadowElevation = 3.dp,
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.action_back),
                                tint = scheme.onSurface,
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier.padding(end = 8.dp),
                        shape = CircleShape,
                        color = if (isFavorite) {
                            scheme.primaryContainer.copy(alpha = 0.94f)
                        } else {
                            scheme.surface.copy(alpha = 0.94f)
                        },
                        contentColor = scheme.onSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isFavorite) {
                                scheme.primary.copy(alpha = 0.28f)
                            } else {
                                scheme.outline.copy(alpha = 0.24f)
                            },
                        ),
                        shadowElevation = 3.dp,
                    ) {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) {
                                    Icons.Rounded.Star
                                } else {
                                    Icons.Rounded.StarBorder
                                },
                                contentDescription = stringResource(
                                    if (isFavorite) {
                                        R.string.action_remove_favorite
                                    } else {
                                        R.string.action_add_favorite
                                    },
                                ),
                                tint = if (isFavorite) scheme.primary else scheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = scheme.surface.copy(alpha = 0.94f),
                    navigationIconContentColor = scheme.onSurface,
                    actionIconContentColor = scheme.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.l, vertical = spacing.s),
            verticalArrangement = Arrangement.spacedBy(spacing.l),
        ) {
            ToolHero(tool)
            content(Modifier)
        }
    }
}

@Composable
private fun ToolHero(tool: Tool) {
    val spacing = LocalSpacing.current
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = Color.Transparent,
        contentColor = scheme.onSurface,
        border = BorderStroke(1.dp, scheme.primary.copy(alpha = 0.20f)),
        shadowElevation = 8.dp,
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(
                        scheme.primaryContainer.copy(alpha = 0.78f),
                        scheme.secondaryContainer.copy(alpha = 0.44f),
                        scheme.surface.copy(alpha = 0.96f),
                    ),
                ),
            ),
        ) {
            Row(
                modifier = Modifier.padding(spacing.l),
                horizontalArrangement = Arrangement.spacedBy(spacing.m),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ToolIconBadge(
                    icon = tool.icon,
                    size = 58.dp,
                    iconSize = 29.dp,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(tool.nameRes),
                        style = MaterialTheme.typography.headlineMedium,
                        color = scheme.onSurface,
                    )
                    Text(
                        text = stringResource(tool.descriptionRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                    )
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = scheme.surface.copy(alpha = 0.72f),
                        contentColor = scheme.onSurfaceVariant,
                        border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.18f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = spacing.s, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = scheme.primary,
                            )
                            Text(
                                text = stringResource(R.string.tool_local_badge),
                                style = MaterialTheme.typography.labelSmall,
                                color = scheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

