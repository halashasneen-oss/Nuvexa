package com.nuvexa.app.ui.tools.qr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.nuvexa.app.R
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing

@Composable
fun QrScannerScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val activity = context as Activity
    var scannedText by remember { mutableStateOf<String?>(null) }

    val scanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val scanResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        if (scanResult?.contents != null) {
            scannedText = scanResult.contents
            onResult("Scanned a code")
        }
    }

    fun launchScan() {
        val intent = IntentIntegrator(activity)
            .setOrientationLocked(false)
            .setBeepEnabled(true)
            .createScanIntent()
        scanLauncher.launch(intent)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchScan()
    }

    fun onScanClick() {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) launchScan() else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (scannedText == null) {
            EmptyState(
                icon = Icons.Filled.QrCodeScanner,
                title = stringResource(R.string.tool_qr_scanner_name),
                body = stringResource(R.string.qr_scan_hint),
            )
            PrimaryButton(text = stringResource(R.string.action_scan), onClick = ::onScanClick, modifier = Modifier.fillMaxWidth())
        } else {
            val value = scannedText.orEmpty()
            ResultCard(
                value = value,
                label = stringResource(R.string.qr_scanned_result),
                onCopy = { context.copyTextToClipboard(value) },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
                if (value.startsWith("http://") || value.startsWith("https://")) {
                    PrimaryButton(
                        text = stringResource(R.string.qr_open_link),
                        onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(value))) } },
                        modifier = Modifier.weight(1f),
                    )
                }
                SecondaryButton(
                    text = stringResource(R.string.qr_scan_again),
                    onClick = { scannedText = null; onScanClick() },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
