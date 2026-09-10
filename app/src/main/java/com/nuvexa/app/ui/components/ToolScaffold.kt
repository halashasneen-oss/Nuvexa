package com.nuvexa.app.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.model.Tool
import com.nuvexa.app.core.util.InterstitialAdManager
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
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme

    // Ad behavior intentionally unchanged: preload on tool entry and offer the interstitial
    // manager the natural exit transition for both toolbar and system-back exits.
    LaunchedEffect(Unit) {
        InterstitialAdManager.preload(context)
    }

    val exitTool = remember(context, onBack) {
        {
            val activity = context.findActivity()
            if (activity != null) {
                InterstitialAdManager.showOnToolExit(activity, onBack)
            } else {
                onBack()
            }
        }
    }

    BackHandler { exitTool() }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = scheme.onBackground,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Surface(
                        modifier = Modifier.padding(start = 8.dp),
                        shape = CircleShape,
                        color = scheme.surface.copy(alpha = 0.92f),
                        contentColor = scheme.onSurface,
                        border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.30f)),
                    ) {
                        IconButton(onClick = exitTool) {
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
                        color = scheme.surface.copy(alpha = 0.92f),
                        contentColor = scheme.onSurface,
                        border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.30f)),
                    ) {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                contentDescription = stringResource(
                                    if (isFavorite) R.string.action_remove_favorite else R.string.action_add_favorite,
                                ),
                                tint = if (isFavorite) scheme.primary else scheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
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
        color = scheme.surface.copy(alpha = 0.90f),
        contentColor = scheme.onSurface,
        border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.30f)),
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(spacing.l),
            horizontalArrangement = Arrangement.spacedBy(spacing.m),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToolIconBadge(icon = tool.icon)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
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
                    color = scheme.primaryContainer.copy(alpha = 0.82f),
                    contentColor = scheme.onPrimaryContainer,
                ) {
                    Text(
                        text = stringResource(R.string.tool_local_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = scheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = spacing.s, vertical = 5.dp),
                    )
                }
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var current: Context = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        val base = current.baseContext
        if (base === current) return null
        current = base
    }
    return current as? Activity
}
