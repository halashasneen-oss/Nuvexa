package com.nuvexa.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.nuvexa.app.ui.theme.LocalNuvexaStatusColors
import com.nuvexa.app.ui.theme.LocalSpacing

enum class FeedbackTone {
    INFO,
    SUCCESS,
    ERROR,
}

@Composable
fun FeedbackCard(
    message: String,
    modifier: Modifier = Modifier,
    tone: FeedbackTone = FeedbackTone.INFO,
) {
    val spacing = LocalSpacing.current
    val scheme = MaterialTheme.colorScheme
    val status = LocalNuvexaStatusColors.current

    val accent: Color
    val container: Color
    val icon: ImageVector

    when (tone) {
        FeedbackTone.INFO -> {
            accent = status.info
            container = status.infoContainer
            icon = Icons.Rounded.Info
        }
        FeedbackTone.SUCCESS -> {
            accent = status.success
            container = status.successContainer
            icon = Icons.Rounded.CheckCircle
        }
        FeedbackTone.ERROR -> {
            accent = scheme.error
            container = scheme.errorContainer
            icon = Icons.Rounded.ErrorOutline
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = container.copy(alpha = 0.82f),
        contentColor = scheme.onSurface,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.24f)),
    ) {
        Row(
            modifier = Modifier.padding(spacing.m),
            horizontalArrangement = Arrangement.spacedBy(spacing.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(21.dp),
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurface,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun ProcessingCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = scheme.primaryContainer.copy(alpha = 0.56f),
        contentColor = scheme.onSurface,
        border = BorderStroke(1.dp, scheme.primary.copy(alpha = 0.20f)),
    ) {
        Row(
            modifier = Modifier.padding(spacing.m),
            horizontalArrangement = Arrangement.spacedBy(spacing.m),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp,
                color = scheme.primary,
            )
            Icon(
                imageVector = Icons.Rounded.HourglassTop,
                contentDescription = null,
                tint = scheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurface,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
