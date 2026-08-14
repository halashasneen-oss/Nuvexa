package com.nuvexa.app.ui.tools.text

import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType

@Composable
fun Base64ToolScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var isEncode by remember { mutableStateOf(true) }
    var input by remember { mutableStateOf("") }

    val output = remember(input, isEncode) {
        runCatching {
            if (isEncode) {
                Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            } else {
                String(Base64.decode(input, Base64.DEFAULT), Charsets.UTF_8)
            }
        }.getOrDefault("")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            FilterChip(selected = isEncode, onClick = { isEncode = true }, label = { Text(stringResource(R.string.mode_encode)) })
            FilterChip(selected = !isEncode, onClick = { isEncode = false }, label = { Text(stringResource(R.string.mode_decode)) })
        }

        NuvexaTextField(value = input, onValueChange = { input = it }, label = stringResource(R.string.input_label), minLines = 4)

        Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
            Text(stringResource(R.string.output_label), style = MaterialTheme.typography.labelLarge)
            Text(output, style = NuvexaExtraType.monospaceBody)
        }

        PrimaryButton(
            text = stringResource(R.string.action_copy),
            onClick = { context.copyTextToClipboard(output) },
            modifier = Modifier.fillMaxWidth(),
            enabled = output.isNotEmpty(),
        )
    }
}
