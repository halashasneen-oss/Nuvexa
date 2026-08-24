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
import android.graphics.pdf.PdfDocument
import com.nuvexa.app.core.util.EncryptedPdfException
import com.nuvexa.app.core.util.addBitmapPage
import com.nuvexa.app.core.util.getPdfPageCount
import com.nuvexa.app.core.util.processPdfPages
import com.nuvexa.app.core.util.savePdfDocument
import com.nuvexa.app.core.util.shareFile
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import java.io.File

@Composable
fun PdfSplitScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var sourceUri by remember { mutableStateOf<Uri?>(null) }
    var pageCount by remember { mutableStateOf(0) }
    var fromPage by remember { mutableStateOf("1") }
    var toPage by remember { mutableStateOf("1") }
    var resultFile by remember { mutableStateOf<File?>(null) }
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
                } else {
                    sourceUri = uri
                    pageCount = count
                    toPage = count.toString()
                    resultFile = null
                    error = null
                }
            }
            .onFailure { error = when (it) { is EncryptedPdfException -> encryptedError; is OutOfMemoryError -> tooLargeError; else -> noPagesError } }
    }

    fun split() {
        val uri = sourceUri ?: return
        val from = (fromPage.toIntOrNull() ?: 1).coerceIn(1, pageCount)
        val to = (toPage.toIntOrNull() ?: pageCount).coerceIn(from, pageCount)
        val document = PdfDocument()
        var pageNumber = 0
        val outcome = runCatching {
            // Only pages inside [from, to] are ever decoded — the rest of the source PDF
            // is skipped entirely, so a large document outside the requested range never
            // touches memory.
            context.processPdfPages(uri, pageRange = (from - 1) until to) { _, bitmap ->
                pageNumber++
                document.addBitmapPage(bitmap, pageNumber)
                true
            }
        }
        outcome.onSuccess { processedCount ->
            if (processedCount == 0) {
                error = noPagesError
                return@onSuccess
            }
            resultFile = context.savePdfDocument(document, "SPLIT")
            onResult("Split pages $from–$to into a new PDF")
        }.onFailure {
            error = when (it) { is EncryptedPdfException -> encryptedError; is OutOfMemoryError -> tooLargeError; else -> noPagesError }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (sourceUri == null) {
            EmptyState(
                icon = ToolCategory.PDF_DOCUMENT.icon,
                title = stringResource(R.string.pdf_pick_file),
                body = stringResource(R.string.tool_pdf_split_desc),
            )
            PrimaryButton(
                text = stringResource(R.string.pdf_pick_file),
                onClick = { pickPdf.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(stringResource(R.string.pdf_page_count_result, pageCount), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
                NuvexaNumberField(value = fromPage, onValueChange = { fromPage = it; resultFile = null }, label = stringResource(R.string.pdf_page_range_from), allowDecimal = false, modifier = Modifier.weight(1f))
                NuvexaNumberField(value = toPage, onValueChange = { toPage = it; resultFile = null }, label = stringResource(R.string.pdf_page_range_to), allowDecimal = false, modifier = Modifier.weight(1f))
            }
            PrimaryButton(text = stringResource(R.string.action_apply), onClick = ::split, modifier = Modifier.fillMaxWidth())
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
        }
    }
}
