package com.nuvexa.app.ui.tools.developer

import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import org.json.JSONObject

private fun decodeJwtSegment(segment: String): String {
    val padded = segment.padEnd((segment.length + 3) / 4 * 4, '=')
    val bytes = Base64.decode(padded, Base64.URL_SAFE or Base64.NO_WRAP)
    val json = JSONObject(String(bytes, Charsets.UTF_8))
    return json.toString(2)
}

@Composable
fun JwtDecoderScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var input by remember { mutableStateOf("") }
    var header by remember { mutableStateOf<String?>(null) }
    var payload by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val invalidMessage = stringResource(R.string.jwt_invalid)

    fun decode() {
        val parts = input.trim().split(".")
        if (parts.size < 2) {
            error = invalidMessage
            header = null
            payload = null
            return
        }
        val decoded = runCatching {
            decodeJwtSegment(parts[0]) to decodeJwtSegment(parts[1])
        }
        decoded.onSuccess { (h, p) ->
            header = h
            payload = p
            error = null
        }.onFailure {
            error = invalidMessage
            header = null
            payload = null
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaTextField(value = input, onValueChange = { input = it }, label = stringResource(R.string.jwt_input), minLines = 4)
        PrimaryButton(text = stringResource(R.string.action_apply), onClick = ::decode, modifier = Modifier.fillMaxWidth(), enabled = input.isNotBlank())

        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }

        header?.let { h ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                Text(stringResource(R.string.jwt_header), style = MaterialTheme.typography.labelLarge)
                Text(h, style = NuvexaExtraType.monospaceBody)
            }
        }
        payload?.let { p ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                Text(stringResource(R.string.jwt_payload), style = MaterialTheme.typography.labelLarge)
                Text(p, style = NuvexaExtraType.monospaceBody)
            }
        }
    }
}
