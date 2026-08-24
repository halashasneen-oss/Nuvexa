package com.nuvexa.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Standard A4 page size in PDF points (1/72 inch) — every PDF Nuvexa creates uses this. */
const val PDF_PAGE_WIDTH_PT = 595
const val PDF_PAGE_HEIGHT_PT = 842

/** Thrown when a PDF can't be opened specifically because it's password-protected —
 * PdfRenderer (and therefore Nuvexa) doesn't support encrypted PDFs. */
class EncryptedPdfException(cause: Throwable? = null) : Exception("This PDF is password-protected.", cause)

private class OpenPdf(private val pfd: ParcelFileDescriptor, val renderer: PdfRenderer) : AutoCloseable {
    override fun close() {
        renderer.close()
        pfd.close()
    }
}

private fun Context.openPdf(uri: Uri): OpenPdf? {
    val pfd = contentResolver.openFileDescriptor(uri, "r") ?: return null
    return try {
        OpenPdf(pfd, PdfRenderer(pfd))
    } catch (e: SecurityException) {
        pfd.close()
        throw EncryptedPdfException(e)
    } catch (e: Exception) {
        pfd.close()
        null
    }
}

fun Context.getPdfPageCount(uri: Uri): Int? = openPdf(uri)?.use { it.renderer.pageCount }

/** Renders every page of the PDF at [uri] to a bitmap, longest edge scaled to roughly
 * [targetLongEdge] px. Capped at [maxPages] pages to keep memory use bounded on huge files.
 * Every page ends up held in memory **at once** — only use this where the UI genuinely needs
 * simultaneous access to all pages (e.g. Organize's reorderable thumbnail list). For any
 * transform-and-rebuild flow (split/merge/rotate/watermark/export), prefer [processPdfPages]
 * which renders, processes, and recycles one page at a time. [Context.renderPdfPage] should be
 * used wherever only a single page is needed (e.g. on-demand viewer rendering). */
fun Context.renderPdfPages(uri: Uri, targetLongEdge: Int = 1000, maxPages: Int = 150): List<Bitmap> {
    val bitmaps = mutableListOf<Bitmap>()
    openPdf(uri)?.use { open ->
        val count = open.renderer.pageCount.coerceAtMost(maxPages)
        for (i in 0 until count) {
            renderOnePage(open.renderer, i, targetLongEdge)?.let { bitmaps.add(it) }
        }
    }
    return bitmaps
}

fun Context.renderPdfPage(uri: Uri, index: Int, targetLongEdge: Int = 1600): Bitmap? =
    openPdf(uri)?.use { open ->
        if (index !in 0 until open.renderer.pageCount) null else renderOnePage(open.renderer, index, targetLongEdge)
    }

/** Streams the pages of the PDF at [uri] through [onPage] one at a time: render a page, hand
 * it to the caller, recycle it, then move on — at most one page's bitmap is ever held in
 * memory, unlike [renderPdfPages]. [onPage] receives the zero-based page index; return `false`
 * to stop early (e.g. once enough pages have been collected). [pageRange] limits which pages
 * are rendered at all (pages outside it are skipped without ever being decoded), which is the
 * key saving for tools like Split that only need a slice of a large PDF. Returns the number of
 * pages actually processed. */
fun Context.processPdfPages(
    uri: Uri,
    targetLongEdge: Int = 1000,
    maxPages: Int = 150,
    pageRange: IntRange? = null,
    onPage: (index: Int, bitmap: Bitmap) -> Boolean = { _, _ -> true },
): Int {
    var processed = 0
    openPdf(uri)?.use { open ->
        val count = open.renderer.pageCount.coerceAtMost(maxPages)
        val range = pageRange?.let { it.first.coerceAtLeast(0)..it.last.coerceAtMost(count - 1) } ?: 0 until count
        for (i in range) {
            val bitmap = renderOnePage(open.renderer, i, targetLongEdge) ?: continue
            val keepGoing = try {
                onPage(i, bitmap)
            } finally {
                bitmap.recycle()
            }
            processed++
            if (!keepGoing) break
        }
    }
    return processed
}

private fun renderOnePage(renderer: PdfRenderer, index: Int, targetLongEdge: Int): Bitmap? {
    val page = renderer.openPage(index)
    return try {
        val scale = targetLongEdge.toFloat() / maxOf(page.width, page.height)
        val width = (page.width * scale).toInt().coerceAtLeast(1)
        val height = (page.height * scale).toInt().coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        bitmap
    } finally {
        page.close()
    }
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    if (degrees % 360 == 0) return bitmap
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

/** Adds one A4 page to [document] from [original] (scaled to fit with a small margin),
 * as page number [pageNumber] (1-based). The caller keeps ownership of [original] — this
 * does not recycle it. Used both by [buildPdfFromBitmaps] (all pages already in memory) and
 * by streaming callers that render/add/recycle one page at a time. */
fun PdfDocument.addBitmapPage(original: Bitmap, pageNumber: Int, rotationDegrees: Int = 0, watermarkText: String? = null) {
    val margin = 24f
    val bitmap = rotateBitmap(original, rotationDegrees)
    val pageInfo = PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH_PT, PDF_PAGE_HEIGHT_PT, pageNumber).create()
    val page = startPage(pageInfo)
    val canvas = page.canvas
    canvas.drawColor(Color.WHITE)

    val availableWidth = PDF_PAGE_WIDTH_PT - margin * 2
    val availableHeight = PDF_PAGE_HEIGHT_PT - margin * 2
    val scale = minOf(availableWidth / bitmap.width, availableHeight / bitmap.height)
    val destWidth = bitmap.width * scale
    val destHeight = bitmap.height * scale
    val left = (PDF_PAGE_WIDTH_PT - destWidth) / 2f
    val top = (PDF_PAGE_HEIGHT_PT - destHeight) / 2f
    canvas.drawBitmap(bitmap, null, RectF(left, top, left + destWidth, top + destHeight), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))

    watermarkText?.let { drawWatermark(canvas, it) }
    finishPage(page)
    if (bitmap !== original) bitmap.recycle()
}

/** Builds a new PDF with one A4 page per bitmap, each scaled to fit with a small margin.
 * [rotations] (degrees, one per bitmap) is applied to the source image before layout.
 * Requires every bitmap to already be in memory at once — prefer streaming the source PDF
 * page-by-page with [processPdfPages] + [addBitmapPage] when the bitmaps come from rendering
 * a (possibly large) source PDF rather than from a bounded, user-picked list of images. */
fun buildPdfFromBitmaps(bitmaps: List<Bitmap>, rotations: List<Int>? = null, watermarkText: String? = null): PdfDocument {
    val document = PdfDocument()
    bitmaps.forEachIndexed { index, bitmap ->
        document.addBitmapPage(bitmap, index + 1, rotations?.getOrNull(index) ?: 0, watermarkText)
    }
    return document
}

private fun drawWatermark(canvas: android.graphics.Canvas, text: String) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(70, 128, 128, 128)
        textSize = 48f
        textAlign = Paint.Align.CENTER
    }
    canvas.save()
    canvas.rotate(-35f, PDF_PAGE_WIDTH_PT / 2f, PDF_PAGE_HEIGHT_PT / 2f)
    canvas.drawText(text, PDF_PAGE_WIDTH_PT / 2f, PDF_PAGE_HEIGHT_PT / 2f, paint)
    canvas.restore()
}

private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
    val lines = mutableListOf<String>()
    text.split("\n").forEach { paragraph ->
        if (paragraph.isEmpty()) {
            lines.add("")
            return@forEach
        }
        var currentLine = StringBuilder()
        for (word in paragraph.split(" ")) {
            val candidate = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(candidate) <= maxWidth) {
                currentLine = StringBuilder(candidate)
                continue
            }
            if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
            currentLine = StringBuilder(word)
            while (paint.measureText(currentLine.toString()) > maxWidth && currentLine.length > 1) {
                var breakIndex = currentLine.length - 1
                while (breakIndex > 1 && paint.measureText(currentLine.substring(0, breakIndex)) > maxWidth) breakIndex--
                lines.add(currentLine.substring(0, breakIndex))
                currentLine = StringBuilder(currentLine.substring(breakIndex))
            }
        }
        lines.add(currentLine.toString())
    }
    return lines
}

/** Paginates [text] across as many A4 pages as needed, using simple word-wrapped body text. */
fun buildPdfFromText(text: String): PdfDocument {
    val document = PdfDocument()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 14f
        color = Color.BLACK
    }
    val margin = 40f
    val maxWidth = PDF_PAGE_WIDTH_PT - margin * 2
    val lines = wrapText(text.ifBlank { " " }, paint, maxWidth)
    val lineHeight = (paint.descent() - paint.ascent()) * 1.3f
    val linesPerPage = ((PDF_PAGE_HEIGHT_PT - margin * 2) / lineHeight).toInt().coerceAtLeast(1)

    var pageNumber = 1
    var start = 0
    while (start < lines.size) {
        val end = (start + linesPerPage).coerceAtMost(lines.size)
        val pageInfo = PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH_PT, PDF_PAGE_HEIGHT_PT, pageNumber).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawColor(Color.WHITE)
        var y = margin - paint.ascent()
        for (i in start until end) {
            canvas.drawText(lines[i], margin, y, paint)
            y += lineHeight
        }
        document.finishPage(page)
        start = end
        pageNumber++
    }
    return document
}

fun Context.savePdfDocument(document: PdfDocument, prefix: String): File {
    val dir = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Nuvexa").apply { mkdirs() }
    val timestamp = SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.US).format(Date())
    val file = File(dir, "${prefix}_$timestamp.pdf")
    FileOutputStream(file).use { out -> document.writeTo(out) }
    document.close()
    return file
}
