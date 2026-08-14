package com.nuvexa.app.ui.tools.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import java.util.UUID

@Composable
fun UuidGeneratorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var uuid by remember { mutableStateOf(UUID.randomUUID().toString()) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        PrimaryButton(
            text = stringResource(R.string.action_generate),
            onClick = { uuid = UUID.randomUUID().toString() },
            modifier = Modifier.fillMaxWidth(),
        )
        ResultCard(
            value = uuid,
            valueStyle = NuvexaExtraType.monospaceBody,
            onCopy = { context.copyTextToClipboard(uuid) },
        )
    }
}
