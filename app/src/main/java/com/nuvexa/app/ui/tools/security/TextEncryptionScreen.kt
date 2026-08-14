package com.nuvexa.app.ui.tools.security

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
import com.nuvexa.app.core.util.TextCipher
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType

private enum class CipherMode { ENCRYPT, DECRYPT }

@Composable
fun TextEncryptionScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var mode by remember { mutableStateOf(CipherMode.ENCRYPT) }
    var password by remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val invalidMessage = stringResource(R.string.encryption_invalid)

    fun run() {
        val result = if (mode == CipherMode.ENCRYPT) {
            TextCipher.encrypt(input, password)
        } else {
            TextCipher.decrypt(input, password)
        }
        result.onSuccess { output = it; error = null }.onFailure { output = null; error = invalidMessage }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            FilterChip(
                selected = mode == CipherMode.ENCRYPT,
                onClick = { mode = CipherMode.ENCRYPT; output = null; error = null },
                label = { Text(stringResource(R.string.mode_encode)) },
            )
            FilterChip(
                selected = mode == CipherMode.DECRYPT,
                onClick = { mode = CipherMode.DECRYPT; output = null; error = null },
                label = { Text(stringResource(R.string.mode_decode)) },
            )
        }

        Text(stringResource(R.string.encryption_note), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        NuvexaTextField(value = password, onValueChange = { password = it }, label = stringResource(R.string.encryption_password_label))
        NuvexaTextField(value = input, onValueChange = { input = it }, label = stringResource(R.string.input_label), minLines = 4)

        PrimaryButton(
            text = stringResource(R.string.action_apply),
            onClick = ::run,
            modifier = Modifier.fillMaxWidth(),
            enabled = password.isNotBlank() && input.isNotBlank(),
        )

        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }

        output?.let { value ->
            ResultCard(
                value = value,
                valueStyle = NuvexaExtraType.monospaceBody,
                onCopy = { context.copyTextToClipboard(value) },
            )
        }
    }
}
