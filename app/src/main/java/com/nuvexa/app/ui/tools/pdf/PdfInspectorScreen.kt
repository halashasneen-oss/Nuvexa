package com.nuvexa.app.ui.tools.pdf

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.util.EncryptedPdfException
import com.nuvexa.app.core.util.fileSizeFromUri
import com.nuvexa.app.core.util.formatBytes
import com.nuvexa.app.core.util.getPdfPageCount
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing

private data class PdfInspectorInfo(
    val totalBytes: Long,
    val pageCount: Int,
)

@Composable
fun PdfInspectorScreen(
    modifier: Modifier = Modifier,
    recordHistory: (String) -> Unit = {},
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var info by remember { mutableStateOf<PdfInspectorInfo?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val encryptedError = stringResource(R.string.pdf_encrypted_error)
    val noPagesError = stringResource(R.string.pdf_no_pages_error)
    val tooLargeError = stringResource(R.string.pdf_too_large_error)

    val pickPdf = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching { context.getPdfPageCount(uri) }
            .onSuccess { count ->
                if (count == null || count == 0) {
                    error = noPagesError
                    info = null
                } else {
                    val value = PdfInspectorInfo(context.fileSizeFromUri(uri), count)
                    info = value
                    error = null
                    recordHistory("$count pages • ${formatBytes(value.totalBytes)}")
                }
            }
            .onFailure {
                info = null
                error = when (it) {
                    is EncryptedPdfException -> encryptedError
                    is OutOfMemoryError -> tooLargeError
                    else -> noPagesError
                }
            }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.l),
    ) {
        Text(
            text = stringResource(R.string.pdf_inspector_help),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        PrimaryButton(
            text = stringResource(R.string.pdf_pick_file),
            onClick = { pickPdf.launch("application/pdf") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        )

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        info?.let { data ->
            ResultCard(
                value = data.pageCount.toString(),
                label = stringResource(R.string.pdf_inspector_pages),
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)),
            ) {
                Column(
                    modifier = Modifier.padding(spacing.m),
                    verticalArrangement = Arrangement.spacedBy(spacing.s),
                ) {
                    InspectorMetric(
                        label = stringResource(R.string.pdf_inspector_total_size),
                        value = formatBytes(data.totalBytes),
                    )
                    InspectorMetric(
                        label = stringResource(R.string.pdf_inspector_average_size),
                        value = formatBytes(data.totalBytes / data.pageCount),
                    )
                }
            }
        }
    }
}

@Composable
private fun InspectorMetric(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}
