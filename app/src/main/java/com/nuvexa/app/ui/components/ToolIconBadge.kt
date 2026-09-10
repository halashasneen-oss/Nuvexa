package com.nuvexa.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Shared branded icon treatment used across Nuvexa. */
@Composable
fun ToolIconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    iconSize: Dp = 26.dp,
    subtle: Boolean = false,
) {
    val shape = RoundedCornerShape(17.dp)
    val scheme = MaterialTheme.colorScheme
    val brush = if (subtle) {
        Brush.linearGradient(listOf(scheme.primaryContainer, scheme.secondaryContainer))
    } else {
        Brush.linearGradient(listOf(scheme.primary, scheme.secondary, scheme.tertiary))
    }

    Box(
        modifier = modifier
            .size(size)
            .then(if (subtle) Modifier else Modifier.shadow(6.dp, shape))
            .clip(shape)
            .background(brush),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = if (subtle) scheme.onPrimaryContainer else scheme.onPrimary,
        )
    }
}
