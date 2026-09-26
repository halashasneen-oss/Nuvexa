package com.nuvexa.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nuvexa.app.ui.theme.LocalSpacing

/**
 * Shared premium container for related controls and content.
 *
 * Keeping this treatment in one component gives calculators, document tools and settings the
 * same visual hierarchy without forcing every screen to own card/elevation/border decisions.
 */
@Composable
fun ContentSurface(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(LocalSpacing.current.m),
    content: @Composable ColumnScope.() -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = scheme.surface.copy(alpha = 0.97f),
        contentColor = scheme.onSurface,
        border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.20f)),
        shadowElevation = 5.dp,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}
