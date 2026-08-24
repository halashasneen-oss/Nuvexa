package com.nuvexa.app.ui.tools.pdf

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nuvexa.app.R
import com.nuvexa.app.core.model.ToolCategory
import com.nuvexa.app.core.util.EncryptedPdfException
import com.nuvexa.app.core.util.PDF_PAGE_HEIGHT_PT
import com.nuvexa.app.core.util.PDF_PAGE_WIDTH_PT
import com.nuvexa.app.core.util.getPdfPageCount
import com.nuvexa.app.core.util.renderPdfPage
import com.nuvexa.app.ui.components.EmptyState
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** How many pages on either side of the current one stay rendered, so swiping feels
 * instant without ever holding the whole document in memory. */
private const val KEEP_AROUND_CURRENT = 1

@Composable
fun PdfViewerScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var sourceUri by remember { mutableStateOf<Uri?>(null) }
    var pageCount by remember { mutableIntStateOf(0) }
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
                    sourceUri = null
                } else {
                    sourceUri = uri
                    pageCount = count
                    error = null
                }
            }
            .onFailure {
                error = when (it) { is EncryptedPdfException -> encryptedError; is OutOfMemoryError -> tooLargeError; else -> noPagesError }
                sourceUri = null
            }
    }

    val uri = sourceUri
    if (uri == null) {
        Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
            EmptyState(
                icon = ToolCategory.PDF_DOCUMENT.icon,
                title = stringResource(R.string.pdf_pick_file),
                body = stringResource(R.string.tool_pdf_viewer_desc),
            )
            PrimaryButton(
                text = stringResource(R.string.pdf_pick_file),
                onClick = { pickPdf.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
            )
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }
        }
    } else {
        // At most a handful of pages (current +/- KEEP_AROUND_CURRENT) are ever rendered —
        // pages that scroll out of that window are recycled immediately, so opening a
        // 300-page PDF costs the same memory as opening a 3-page one.
        val pageCache = remember(uri) { mutableStateMapOf<Int, Bitmap>() }
        val pagerState = rememberPagerState(pageCount = { pageCount })

        DisposableEffect(uri) {
            onDispose {
                pageCache.values.forEach { it.recycle() }
                pageCache.clear()
            }
        }

        LaunchedEffect(uri, pagerState.currentPage, pageCount) {
            val current = pagerState.currentPage
            val keep = (current - KEEP_AROUND_CURRENT)..(current + KEEP_AROUND_CURRENT)
            pageCache.keys.filter { it !in keep }.forEach { staleIndex ->
                pageCache.remove(staleIndex)?.recycle()
            }
            for (index in keep) {
                if (index !in 0 until pageCount || pageCache.containsKey(index)) continue
                val bitmap = withContext(Dispatchers.IO) { context.renderPdfPage(uri, index) }
                if (bitmap != null) pageCache[index] = bitmap
            }
        }

        Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.s)) {
            Text(
                stringResource(R.string.pdf_page_label, pagerState.currentPage + 1) + " / $pageCount",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(PDF_PAGE_WIDTH_PT.toFloat() / PDF_PAGE_HEIGHT_PT),
            ) { index ->
                val bitmap = pageCache[index]
                if (bitmap == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
