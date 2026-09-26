package com.nuvexa.app.ui.components

import androidx.compose.foundation.BorderStroke
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
        color = scheme.primary,
        contentColor = scheme.onPrimary,
        border = BorderStroke(1.dp, scheme.onPrimary.copy(alpha = 0.12f)),
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            scheme.primary,
                            scheme.secondary,
                            scheme.tertiary,
                        ),
                    ),
                )
                .padding(horizontal = spacing.l, vertical = spacing.xl),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.m),
        ) {
            if (label != null) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = scheme.onPrimary.copy(alpha = 0.12f),
                    contentColor = scheme.onPrimary,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = spacing.s, vertical = 6.dp),
                    )
                }
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
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = scheme.onPrimary.copy(alpha = 0.10f),
                                contentColor = scheme.onPrimary,
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                        ) {
                            Icon(
                                Icons.Rounded.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.padding(end = spacing.xs),
                            )
                            Text(stringResource(R.string.action_copy))
                        }
                    }
                    if (onShare != null) {
                        TextButton(
                            onClick = onShare,
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = scheme.onPrimary.copy(alpha = 0.10f),
                                contentColor = scheme.onPrimary,
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                        ) {
                            Icon(
                                Icons.Rounded.Share,
                                contentDescription = null,
                                modifier = Modifier.padding(end = spacing.xs),
                            )
                            Text(stringResource(R.string.action_share))
                        }
                    }
                }
            }
        }
    }
}
