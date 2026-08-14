package com.nuvexa.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
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
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(spacing.l),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(
                text = value,
                style = valueStyle,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            if (onCopy != null || onShare != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (onCopy != null) {
                        TextButton(onClick = onCopy) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                            Text(stringResource(R.string.action_copy))
                        }
                    }
                    if (onShare != null) {
                        TextButton(onClick = onShare) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                            Text(stringResource(R.string.action_share))
                        }
                    }
                }
            }
        }
    }
}
