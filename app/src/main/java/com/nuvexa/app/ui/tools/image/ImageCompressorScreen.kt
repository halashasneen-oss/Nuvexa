package com.nuvexa.app.ui.tools.image

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.util.fileSizeFromUri
import com.nuvexa.app.core.util.formatBytes
import com.nuvexa.app.core.util.loadBitmapDownsampled
import com.nuvexa.app.core.util.saveJpegToAppPictures
import com.nuvexa.app.core.util.shareFile
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import java.io.File

@Composable
fun ImageCompressorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var originalSize by remember { mutableStateOf(0L) }
    var quality by remember { mutableStateOf(75f) }
    var resultFile by remember { mutableStateOf<File?>(null) }

    val previewBitmap = remember(imageUri) { imageUri?.let { context.loadBitmapDownsampled(it, maxDimension = 720) } }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            originalSize = context.fileSizeFromUri(uri)
            resultFile = null
        }
    }

    fun compress() {
        val uri = imageUri ?: return
        val bitmap = context.loadBitmapDownsampled(uri, maxDimension = 4096) ?: return
        val file = context.saveJpegToAppPictures(bitmap, "COMPRESSED", quality.toInt())
        resultFile = file
        onResult("Compressed an image to ${formatBytes(file.length())}")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (imageUri == null) {
            EmptyState(
                icon = ToolCategory.IMAGE.icon,
                title = stringResource(R.string.image_pick_source),
                body = stringResource(R.string.tool_image_compressor_desc),
            )
            PrimaryButton(
                text = stringResource(R.string.image_pick_source),
                onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            previewBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.6f)
                        .clip(RoundedCornerShape(16.dp)),
                )
            }

            Text(stringResource(R.string.image_original_size, formatBytes(originalSize)), style = MaterialTheme.typography.bodyMedium)

            Text("${quality.toInt()}%", style = MaterialTheme.typography.labelLarge)
            Slider(value = quality, onValueChange = { quality = it; resultFile = null }, valueRange = 10f..100f)

            PrimaryButton(text = stringResource(R.string.action_apply), onClick = ::compress, modifier = Modifier.fillMaxWidth())

            resultFile?.let { file ->
                val savingsPercent = if (originalSize > 0) (100 - (file.length() * 100 / originalSize)).toInt() else 0
                Text(stringResource(R.string.image_result_size, formatBytes(file.length())), style = MaterialTheme.typography.bodyMedium)
                if (savingsPercent > 0) {
                    Text(
                        stringResource(R.string.image_savings, savingsPercent),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    stringResource(R.string.tool_output_saved_to, file.parentFile?.name.orEmpty()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                SecondaryButton(
                    text = stringResource(R.string.action_share),
                    onClick = { context.shareFile(file, "image/jpeg") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SecondaryButton(
                text = stringResource(R.string.action_reset),
                onClick = { imageUri = null; resultFile = null },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
