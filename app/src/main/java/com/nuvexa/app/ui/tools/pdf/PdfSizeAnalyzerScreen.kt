package com.nuvexa.app.ui.tools.pdf

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.util.EncryptedPdfException
import com.nuvexa.app.core.util.fileSizeFromUri
import com.nuvexa.app.core.util.formatBytes
import com.nuvexa.app.core.util.getPdfPageCount
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.theme.LocalSpacing

private data class PdfSizeInfo(val totalBytes: Long, val pageCount: Int)

@Composable
fun PdfSizeAnalyzerScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var info by remember { mutableStateOf<PdfSizeInfo?>(null) }
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
                    info = PdfSizeInfo(context.fileSizeFromUri(uri), count)
                    error = null
                }
            }
            .onFailure { error = when (it) { is EncryptedPdfException -> encryptedError; is OutOfMemoryError -> tooLargeError; else -> noPagesError } }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (info == null) {
            EmptyState(
                icon = ToolCategory.PDF_DOCUMENT.icon,
                title = stringResource(R.string.pdf_pick_file),
                body = stringResource(R.string.tool_pdf_size_analyzer_desc),
            )
        }
        PrimaryButton(
            text = stringResource(R.string.pdf_pick_file),
            onClick = { pickPdf.launch("application/pdf") },
            modifier = Modifier.fillMaxWidth(),
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }

        info?.let { data ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.device_info_storage), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatBytes(data.totalBytes), style = MaterialTheme.typography.titleMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.tool_pdf_page_counter_name), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(data.pageCount.toString(), style = MaterialTheme.typography.titleMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.pdf_average_page_size), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatBytes(data.totalBytes / data.pageCount), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
