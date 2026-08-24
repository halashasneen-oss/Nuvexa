package com.nuvexa.app.ui.tools.pdf

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.core.model.ToolCategory
import android.graphics.pdf.PdfDocument
import com.nuvexa.app.core.util.EncryptedPdfException
import com.nuvexa.app.core.util.addBitmapPage
import com.nuvexa.app.core.util.processPdfPages
import com.nuvexa.app.core.util.savePdfDocument
import com.nuvexa.app.core.util.shareFile
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import java.io.File

@Composable
fun PdfMergeScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var pdfUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var resultFile by remember { mutableStateOf<File?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val encryptedError = stringResource(R.string.pdf_encrypted_error)
    val noPagesError = stringResource(R.string.pdf_no_pages_error)
    val tooLargeError = stringResource(R.string.pdf_too_large_error)

    val pickPdfs = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            pdfUris = pdfUris + uris
            resultFile = null
        }
    }

    fun merge() {
        val document = PdfDocument()
        var pageNumber = 0
        // Each source PDF is streamed one page at a time — only a single page's bitmap is
        // ever in memory, regardless of how many files or how large they are.
        val outcome = runCatching {
            pdfUris.forEach { uri ->
                context.processPdfPages(uri) { _, bitmap ->
                    pageNumber++
                    document.addBitmapPage(bitmap, pageNumber)
                    true
                }
            }
        }
        outcome.onSuccess {
            if (pageNumber == 0) {
                error = noPagesError
                return@onSuccess
            }
            resultFile = context.savePdfDocument(document, "MERGED")
            onResult("Merged ${pdfUris.size} PDFs")
            error = null
        }.onFailure {
            error = when (it) { is EncryptedPdfException -> encryptedError; is OutOfMemoryError -> tooLargeError; else -> noPagesError }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (pdfUris.isEmpty()) {
            EmptyState(
                icon = ToolCategory.PDF_DOCUMENT.icon,
                title = stringResource(R.string.pdf_pick_files),
                body = stringResource(R.string.tool_pdf_merge_desc),
            )
        } else {
            Text(stringResource(R.string.pdf_files_to_merge, pdfUris.size), style = MaterialTheme.typography.titleMedium)
        }

        PrimaryButton(
            text = if (pdfUris.isEmpty()) stringResource(R.string.pdf_pick_files) else stringResource(R.string.pdf_add_another),
            onClick = { pickPdfs.launch("application/pdf") },
            modifier = Modifier.fillMaxWidth(),
        )

        if (pdfUris.isNotEmpty()) {
            PrimaryButton(text = stringResource(R.string.action_apply), onClick = ::merge, modifier = Modifier.fillMaxWidth())
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }

        resultFile?.let { file ->
            Text(
                stringResource(R.string.tool_output_saved_to, file.parentFile?.name.orEmpty()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SecondaryButton(
                text = stringResource(R.string.action_share),
                onClick = { context.shareFile(file, "application/pdf") },
                modifier = Modifier.fillMaxWidth(),
            )
            SecondaryButton(
                text = stringResource(R.string.action_reset),
                onClick = { pdfUris = emptyList(); resultFile = null },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
