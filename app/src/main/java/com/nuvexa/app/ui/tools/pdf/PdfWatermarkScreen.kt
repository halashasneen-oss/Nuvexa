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
import com.nuvexa.app.core.util.EncryptedPdfException
import com.nuvexa.app.core.util.buildPdfFromBitmaps
import com.nuvexa.app.core.util.renderPdfPages
import com.nuvexa.app.core.util.savePdfDocument
import com.nuvexa.app.core.util.shareFile
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.NuvexaTextField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import java.io.File

@Composable
fun PdfWatermarkScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var sourceUri by remember { mutableStateOf<Uri?>(null) }
    var watermarkText by remember { mutableStateOf("") }
    var resultFile by remember { mutableStateOf<File?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val encryptedError = stringResource(R.string.pdf_encrypted_error)
    val noPagesError = stringResource(R.string.pdf_no_pages_error)
    val tooLargeError = stringResource(R.string.pdf_too_large_error)

    val pickPdf = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            sourceUri = uri
            resultFile = null
            error = null
        }
    }

    fun apply() {
        val uri = sourceUri ?: return
        val pages = runCatching { context.renderPdfPages(uri) }
        pages.onSuccess { bitmaps ->
            if (bitmaps.isEmpty()) {
                error = noPagesError
                return@onSuccess
            }
            val document = buildPdfFromBitmaps(bitmaps, watermarkText = watermarkText)
            resultFile = context.savePdfDocument(document, "WATERMARKED")
            onResult("Added a watermark to a PDF")
        }.onFailure {
            error = when (it) { is EncryptedPdfException -> encryptedError; is OutOfMemoryError -> tooLargeError; else -> noPagesError }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (sourceUri == null) {
            EmptyState(
                icon = ToolCategory.PDF_DOCUMENT.icon,
                title = stringResource(R.string.pdf_pick_file),
                body = stringResource(R.string.tool_pdf_watermark_desc),
            )
            PrimaryButton(
                text = stringResource(R.string.pdf_pick_file),
                onClick = { pickPdf.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            NuvexaTextField(value = watermarkText, onValueChange = { watermarkText = it; resultFile = null }, label = stringResource(R.string.pdf_watermark_text_label))
            PrimaryButton(
                text = stringResource(R.string.action_apply),
                onClick = ::apply,
                modifier = Modifier.fillMaxWidth(),
                enabled = watermarkText.isNotBlank(),
            )
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
