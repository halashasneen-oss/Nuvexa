package com.nuvexa.app.ui.tools.pdf

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.util.EncryptedPdfException
import com.nuvexa.app.core.util.buildPdfFromBitmaps
import com.nuvexa.app.core.util.renderPdfPages
import com.nuvexa.app.core.util.savePdfDocument
import com.nuvexa.app.core.util.shareFile
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import java.io.File

@Composable
fun PdfOrganizeScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var pages by remember { mutableStateOf<List<Bitmap>?>(null) }
    var resultFile by remember { mutableStateOf<File?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val encryptedError = stringResource(R.string.pdf_encrypted_error)
    val noPagesError = stringResource(R.string.pdf_no_pages_error)
    val tooLargeError = stringResource(R.string.pdf_too_large_error)

    val pickPdf = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching { context.renderPdfPages(uri, targetLongEdge = 600) }
            .onSuccess { rendered ->
                if (rendered.isEmpty()) { error = noPagesError; pages = null } else { pages = rendered; error = null; resultFile = null }
            }
            .onFailure {
                error = when (it) { is EncryptedPdfException -> encryptedError; is OutOfMemoryError -> tooLargeError; else -> noPagesError }
                pages = null
            }
    }

    fun export() {
        val current = pages ?: return
        if (current.isEmpty()) return
        val document = buildPdfFromBitmaps(current)
        resultFile = context.savePdfDocument(document, "ORGANIZED")
        onResult("Reorganized a PDF (${current.size} pages)")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        val current = pages
        if (current == null) {
            EmptyState(
                icon = ToolCategory.PDF_DOCUMENT.icon,
                title = stringResource(R.string.pdf_pick_file),
                body = stringResource(R.string.tool_pdf_organize_desc),
            )
            PrimaryButton(
                text = stringResource(R.string.pdf_pick_file),
                onClick = { pickPdf.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(stringResource(R.string.pdf_organize_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            current.forEachIndexed { index, bitmap ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(56.dp)
                                    .padding(spacing.s),
                            )
                            Text(
                                stringResource(R.string.pdf_page_label, index + 1),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Row {
                            IconButton(
                                onClick = { if (index > 0) pages = current.toMutableList().apply { add(index - 1, removeAt(index)) } },
                                enabled = index > 0,
                            ) { Icon(Icons.Filled.ArrowUpward, contentDescription = null) }
                            IconButton(
                                onClick = { if (index < current.lastIndex) pages = current.toMutableList().apply { add(index + 1, removeAt(index)) } },
                                enabled = index < current.lastIndex,
                            ) { Icon(Icons.Filled.ArrowDownward, contentDescription = null) }
                            IconButton(onClick = { pages = current.toMutableList().apply { removeAt(index) } }) {
                                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                            }
                        }
                    }
                }
            }

            PrimaryButton(
                text = stringResource(R.string.action_apply),
                onClick = ::export,
                modifier = Modifier.fillMaxWidth(),
                enabled = current.isNotEmpty(),
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
