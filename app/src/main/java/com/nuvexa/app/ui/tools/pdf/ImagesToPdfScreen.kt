package com.nuvexa.app.ui.tools.pdf

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import com.nuvexa.app.core.util.buildPdfFromBitmaps
import com.nuvexa.app.core.util.loadBitmapDownsampled
import com.nuvexa.app.core.util.savePdfDocument
import com.nuvexa.app.core.util.shareFile
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import java.io.File

@Composable
fun ImagesToPdfScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var resultFile by remember { mutableStateOf<File?>(null) }

    val pickImages = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(30)) { uris ->
        if (uris.isNotEmpty()) {
            imageUris = uris
            resultFile = null
        }
    }

    fun build() {
        val bitmaps = imageUris.mapNotNull { context.loadBitmapDownsampled(it, maxDimension = 2000) }
        if (bitmaps.isEmpty()) return
        val document = buildPdfFromBitmaps(bitmaps)
        resultFile = context.savePdfDocument(document, "IMAGES")
        onResult("Created a PDF from ${bitmaps.size} images")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (imageUris.isEmpty()) {
            EmptyState(
                icon = ToolCategory.PDF_DOCUMENT.icon,
                title = stringResource(R.string.pdf_pick_images),
                body = stringResource(R.string.tool_pdf_images_to_pdf_desc),
            )
            PrimaryButton(
                text = stringResource(R.string.pdf_pick_images),
                onClick = { pickImages.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(
                stringResource(R.string.average_count, imageUris.size),
                style = MaterialTheme.typography.labelLarge,
            )
            PrimaryButton(text = stringResource(R.string.action_apply), onClick = ::build, modifier = Modifier.fillMaxWidth())

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

            SecondaryButton(
                text = stringResource(R.string.action_reset),
                onClick = { imageUris = emptyList(); resultFile = null },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
