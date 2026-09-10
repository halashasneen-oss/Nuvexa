package com.nuvexa.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType

@Composable
fun ResultCard(
    value: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    valueStyle: TextStyle = NuvexaExtraType.numericEmphasis,
    onCopy: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
) {
    val spacing = LocalSpacing.current
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = Color.Transparent,
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(scheme.primary, scheme.secondary, scheme.tertiary)))
                .padding(spacing.l),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = scheme.onPrimary.copy(alpha = 0.82f),
                )
            }
            Text(
                text = value,
                style = valueStyle,
                textAlign = TextAlign.Center,
                color = scheme.onPrimary,
            )
            if (onCopy != null || onShare != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (onCopy != null) {
                        TextButton(
                            onClick = onCopy,
                            colors = ButtonDefaults.textButtonColors(contentColor = scheme.onPrimary),
                        ) {
                            Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                            Text(stringResource(R.string.action_copy))
                        }
                    }
                    if (onShare != null) {
                        TextButton(
                            onClick = onShare,
                            colors = ButtonDefaults.textButtonColors(contentColor = scheme.onPrimary),
                        ) {
                            Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                            Text(stringResource(R.string.action_share))
                        }
                    }
                }
            }
        }
    }
}
