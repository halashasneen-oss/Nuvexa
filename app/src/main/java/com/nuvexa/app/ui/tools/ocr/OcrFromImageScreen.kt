package com.nuvexa.app.ui.tools.ocr

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.core.util.loadBitmapDownsampled
import com.nuvexa.app.core.util.recognizeTextInImage
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import kotlinx.coroutines.launch

@Composable
fun OcrFromImageScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val noTextError = stringResource(R.string.ocr_no_text_found)

    fun runOcr(uri: Uri) {
        val bitmap = context.loadBitmapDownsampled(uri, maxDimension = 2200) ?: return
        isProcessing = true
        resultText = null
        error = null
        scope.launch {
            runCatching { recognizeTextInImage(bitmap) }
                .onSuccess { text ->
                    isProcessing = false
                    if (text.isBlank()) {
                        error = noTextError
                    } else {
                        resultText = text
                        onResult("Extracted text from an image")
                    }
                }
                .onFailure {
                    isProcessing = false
                    error = noTextError
                }
        }
    }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) runOcr(uri)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (resultText == null && !isProcessing) {
            EmptyState(
                icon = ToolCategory.OCR.icon,
                title = stringResource(R.string.ocr_pick_source),
                body = stringResource(R.string.tool_ocr_from_image_desc),
            )
            PrimaryButton(
                text = stringResource(R.string.ocr_pick_source),
                onClick = { pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (isProcessing) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
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
            PrimaryButton(
                text = stringResource(R.string.ocr_pick_source),
                onClick = { pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
