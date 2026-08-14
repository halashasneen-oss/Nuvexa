package com.nuvexa.app.ui.tools.image

import android.graphics.Bitmap
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.util.loadBitmapDownsampled
import com.nuvexa.app.core.util.saveJpegToAppPictures
import com.nuvexa.app.core.util.shareFile
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import java.io.File

@Composable
fun ImageResizerScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var sourceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var widthInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var lockAspect by remember { mutableStateOf(true) }
    var resultFile by remember { mutableStateOf<File?>(null) }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            val bitmap = context.loadBitmapDownsampled(uri, maxDimension = 4096)
            sourceBitmap = bitmap
            widthInput = bitmap?.width?.toString().orEmpty()
            heightInput = bitmap?.height?.toString().orEmpty()
            resultFile = null
        }
    }

    fun onWidthChange(newValue: String) {
        widthInput = newValue
        val bitmap = sourceBitmap
        if (lockAspect && bitmap != null) {
            val w = newValue.toIntOrNull()
            if (w != null && bitmap.width > 0) {
                heightInput = (w * bitmap.height / bitmap.width).toString()
            }
        }
    }

    fun onHeightChange(newValue: String) {
        heightInput = newValue
        val bitmap = sourceBitmap
        if (lockAspect && bitmap != null) {
            val h = newValue.toIntOrNull()
            if (h != null && bitmap.height > 0) {
                widthInput = (h * bitmap.width / bitmap.height).toString()
            }
        }
    }

    fun resize() {
        val bitmap = sourceBitmap ?: return
        val w = widthInput.toIntOrNull()?.coerceAtLeast(1) ?: return
        val h = heightInput.toIntOrNull()?.coerceAtLeast(1) ?: return
        val resized = Bitmap.createScaledBitmap(bitmap, w, h, true)
        val file = context.saveJpegToAppPictures(resized, "RESIZED", 92)
        resultFile = file
        onResult("Resized an image to ${w}×$h")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        if (imageUri == null) {
            EmptyState(
                icon = ToolCategory.IMAGE.icon,
                title = stringResource(R.string.image_pick_source),
                body = stringResource(R.string.tool_image_resizer_desc),
            )
            PrimaryButton(
                text = stringResource(R.string.image_pick_source),
                onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            sourceBitmap?.let { bitmap ->
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

            Row(horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
                NuvexaNumberField(value = widthInput, onValueChange = ::onWidthChange, label = "W", allowDecimal = false, modifier = Modifier.weight(1f))
                NuvexaNumberField(value = heightInput, onValueChange = ::onHeightChange, label = "H", allowDecimal = false, modifier = Modifier.weight(1f))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = lockAspect, onCheckedChange = { lockAspect = it })
                Text(stringResource(R.string.tool_image_resizer_name))
            }

            PrimaryButton(text = stringResource(R.string.action_apply), onClick = ::resize, modifier = Modifier.fillMaxWidth())

            resultFile?.let { file ->
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
                onClick = { imageUri = null; sourceBitmap = null; resultFile = null },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
