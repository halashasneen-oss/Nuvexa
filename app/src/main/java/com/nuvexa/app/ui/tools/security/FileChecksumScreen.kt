package com.nuvexa.app.ui.tools.security

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class FileChecksums(val sha256: String, val sha512: String)

@Composable
fun FileChecksumScreen(
    modifier: Modifier = Modifier,
    recordHistory: (String) -> Unit = {},
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var fileName by remember { mutableStateOf<String?>(null) }
    var checksums by remember { mutableStateOf<FileChecksums?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val genericError = stringResource(R.string.file_checksum_error)

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val displayName = context.displayName(uri) ?: context.getString(R.string.file_checksum_unknown_file)
        fileName = displayName
        checksums = null
        error = null
        loading = true
        scope.launch {
            runCatching { withContext(Dispatchers.IO) { computeChecksums(context, uri) } }
                .onSuccess { result ->
                    checksums = result
                    error = null
                    recordHistory(displayName)
                }
                .onFailure {
                    checksums = null
                    error = genericError
                }
            loading = false
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.l),
    ) {
        Text(
            text = stringResource(R.string.file_checksum_privacy),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        PrimaryButton(
            text = stringResource(R.string.file_checksum_pick),
            onClick = { picker.launch("*/*") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.InsertDriveFile, contentDescription = null) },
        )

        fileName?.let {
            Text(
                text = stringResource(R.string.file_checksum_selected, it),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        if (loading) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.file_checksum_reading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        checksums?.let { result ->
            ResultCard(
                value = result.sha256,
                label = "SHA-256",
                valueStyle = NuvexaExtraType.monospaceBody,
                onCopy = { context.copyTextToClipboard(result.sha256) },
            )
            ResultCard(
                value = result.sha512,
                label = "SHA-512",
                valueStyle = NuvexaExtraType.monospaceBody,
                onCopy = { context.copyTextToClipboard(result.sha512) },
            )
        }
    }
}

private fun computeChecksums(context: Context, uri: Uri): FileChecksums {
    val sha256 = MessageDigest.getInstance("SHA-256")
    val sha512 = MessageDigest.getInstance("SHA-512")
    val stream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Unable to open file")

    stream.use { input ->
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val read = input.read(buffer)
            if (read <= 0) break
            sha256.update(buffer, 0, read)
            sha512.update(buffer, 0, read)
        }
    }

    return FileChecksums(sha256.digest().toHex(), sha512.digest().toHex())
}

private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it.toInt() and 0xff) }

private fun Context.displayName(uri: Uri): String? =
    contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    }
