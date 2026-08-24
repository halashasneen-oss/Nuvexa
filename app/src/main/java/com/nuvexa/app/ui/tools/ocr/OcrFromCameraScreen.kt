package com.nuvexa.app.ui.tools.ocr

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import java.io.File

@Composable
fun OcrFromCameraScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val noTextError = stringResource(R.string.ocr_no_text_found)
    val cameraUnavailableError = stringResource(R.string.error_camera_unavailable)

    fun runOcr(uri: Uri) {
        val bitmap = context.loadBitmapDownsampled(uri, maxDimension = 2200) ?: return
        isProcessing = true
        resultText = null
        error = null
        scope.launch {
            val outcome = try {
                runCatching { recognizeTextInImage(bitmap) }
            } finally {
                bitmap.recycle()
            }
            outcome
                .onSuccess { text ->
                    isProcessing = false
                    if (text.isBlank()) error = noTextError else { resultText = text; onResult("Extracted text from a photo") }
                }
                .onFailure { isProcessing = false; error = noTextError }
        }
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = pendingPhotoUri
        if (success && uri != null) runOcr(uri) else error = cameraUnavailableError
    }

    fun launchCamera() {
        val sharedDir = File(context.cacheDir, "shared").apply { mkdirs() }
        val file = File(sharedDir, "ocr_capture_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        pendingPhotoUri = uri
        takePicture.launch(uri)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera() else error = cameraUnavailableError
    }

    fun onTakePhotoClick() {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) launchCamera() else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (resultText == null && !isProcessing) {
            EmptyState(
                icon = ToolCategory.OCR.icon,
                title = stringResource(R.string.ocr_take_photo),
                body = stringResource(R.string.tool_ocr_from_camera_desc),
            )
            PrimaryButton(text = stringResource(R.string.ocr_take_photo), onClick = ::onTakePhotoClick, modifier = Modifier.fillMaxWidth())
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
            PrimaryButton(text = stringResource(R.string.ocr_take_photo), onClick = ::onTakePhotoClick, modifier = Modifier.fillMaxWidth())
        }
    }
}
