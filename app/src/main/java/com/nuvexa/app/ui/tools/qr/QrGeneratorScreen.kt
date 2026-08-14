package com.nuvexa.app.ui.tools.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nuvexa.app.R
import com.nuvexa.app.core.util.buildWifiQrPayload
import com.nuvexa.app.core.util.generateQrBitmap
import com.nuvexa.app.core.util.saveBitmapToAppPictures
import com.nuvexa.app.core.util.shareFile
import androidx.compose.material3.Text
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import android.widget.Toast

private enum class QrMode { TEXT, URL, WIFI }

@Composable
fun QrGeneratorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var mode by remember { mutableStateOf(QrMode.TEXT) }
    var text by remember { mutableStateOf("") }
    var ssid by remember { mutableStateOf("") }
    var wifiPassword by remember { mutableStateOf("") }
    var secured by remember { mutableStateOf(true) }

    val payload = when (mode) {
        QrMode.TEXT, QrMode.URL -> text
        QrMode.WIFI -> if (ssid.isBlank()) "" else buildWifiQrPayload(ssid, wifiPassword, secured)
    }

    val bitmap = remember(payload) { generateQrBitmap(payload) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
            FilterChip(selected = mode == QrMode.TEXT, onClick = { mode = QrMode.TEXT }, label = { Text(stringResource(R.string.qr_mode_text)) })
            FilterChip(selected = mode == QrMode.URL, onClick = { mode = QrMode.URL }, label = { Text(stringResource(R.string.qr_mode_url)) })
            FilterChip(selected = mode == QrMode.WIFI, onClick = { mode = QrMode.WIFI }, label = { Text(stringResource(R.string.qr_mode_wifi)) })
        }

        when (mode) {
            QrMode.TEXT, QrMode.URL -> NuvexaTextField(value = text, onValueChange = { text = it }, label = stringResource(R.string.input_label), minLines = 2)
            QrMode.WIFI -> {
                NuvexaTextField(value = ssid, onValueChange = { ssid = it }, label = stringResource(R.string.qr_wifi_ssid))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = secured, onCheckedChange = { secured = it })
                    Text(stringResource(R.string.qr_wifi_secured))
                }
                if (secured) {
                    NuvexaTextField(value = wifiPassword, onValueChange = { wifiPassword = it }, label = stringResource(R.string.qr_wifi_password))
                }
            }
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
                val savedMessage = stringResource(R.string.action_saved)
                PrimaryButton(
                    text = stringResource(R.string.action_save),
                    onClick = {
                        context.saveBitmapToAppPictures(bitmap, "QR")
                        onResult("Generated a QR code")
                        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(
                    text = stringResource(R.string.action_share),
                    onClick = {
                        val file = context.saveBitmapToAppPictures(bitmap, "QR")
                        context.shareFile(file, "image/png")
                        onResult("Generated a QR code")
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
