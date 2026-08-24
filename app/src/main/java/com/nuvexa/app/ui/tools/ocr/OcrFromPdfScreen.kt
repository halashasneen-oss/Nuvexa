package com.nuvexa.app.ui.tools.ocr

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.util.EncryptedPdfException
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.core.util.getPdfPageCount
import com.nuvexa.app.core.util.recognizeTextInImage
import com.nuvexa.app.core.util.renderPdfPage
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import kotlinx.coroutines.launch

private object NoPdfPagesException : Exception("This PDF has no pages.")

@Composable
fun OcrFromPdfScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val encryptedError = stringResource(R.string.pdf_encrypted_error)
    val noPagesError = stringResource(R.string.pdf_no_pages_error)
    val tooLargeError = stringResource(R.string.pdf_too_large_error)
    val noTextError = stringResource(R.string.ocr_no_text_found)
    val pageLabelTemplate = stringResource(R.string.pdf_page_label)

    val pickPdf = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        isProcessing = true
        resultText = null
        error = null
        scope.launch {
            // One page is rendered, OCR'd, and released before the next page is rendered —
            // only a single page's bitmap is ever in memory, unlike batch-rendering the
            // whole PDF up front.
            val outcome = runCatching {
                val count = (context.getPdfPageCount(uri) ?: 0).coerceAtMost(150)
                if (count == 0) throw NoPdfPagesException
                val texts = mutableListOf<String>()
                for (index in 0 until count) {
                    val bitmap = context.renderPdfPage(uri, index, targetLongEdge = 1400) ?: continue
                    val text = try {
                        runCatching { recognizeTextInImage(bitmap) }.getOrDefault("")
                    } finally {
                        bitmap.recycle()
                    }
                    if (text.isNotBlank()) texts.add("${pageLabelTemplate.format(index + 1)}\n$text")
                }
                texts
            }
            outcome.onSuccess { texts ->
                isProcessing = false
                if (texts.isEmpty()) {
                    error = noTextError
                } else {
                    resultText = texts.joinToString("\n\n")
                    onResult("Extracted text from a scanned PDF")
                }
            }.onFailure {
                isProcessing = false
                error = when (it) {
                    is EncryptedPdfException -> encryptedError
                    is OutOfMemoryError -> tooLargeError
                    else -> noPagesError
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (resultText == null && !isProcessing) {
            EmptyState(
                icon = ToolCategory.OCR.icon,
                title = stringResource(R.string.pdf_pick_file),
                body = stringResource(R.string.tool_ocr_from_pdf_desc),
            )
            PrimaryButton(
                text = stringResource(R.string.pdf_pick_file),
                onClick = { pickPdf.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (isProcessing) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator()
                Text(stringResource(R.string.ocr_processing), style = MaterialTheme.typography.bodyMedium)
            }
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }

        resultText?.let { text ->
            ResultCard(
                value = text,
                valueStyle = MaterialTheme.typography.bodyLarge,
                onCopy = { context.copyTextToClipboard(text) },
            )
        }
    }
}
