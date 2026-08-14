package com.nuvexa.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns

/** Decodes an image from [uri], downsampling so it never fully loads at a larger
 * resolution than [maxDimension] — this is what keeps large photos from causing an
 * out-of-memory crash. */
fun Context.loadBitmapDownsampled(uri: Uri, maxDimension: Int = 2048): Bitmap? {
    return runCatching {
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, boundsOptions)
        }
        val (width, height) = boundsOptions.outWidth to boundsOptions.outHeight
        if (width <= 0 || height <= 0) return null

        var sampleSize = 1
        while (width / sampleSize > maxDimension || height / sampleSize > maxDimension) {
            sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        }
    }.getOrNull()
}

fun Context.fileSizeFromUri(uri: Uri): Long {
    return runCatching {
        contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst() && index >= 0) cursor.getLong(index) else 0L
        } ?: 0L
    }.getOrDefault(0L)
}

fun Bitmap.scaledTo(maxDimension: Int): Bitmap {
    if (width <= maxDimension && height <= maxDimension) return this
    val ratio = minOf(maxDimension.toFloat() / width, maxDimension.toFloat() / height)
    val newWidth = (width * ratio).toInt().coerceAtLeast(1)
    val newHeight = (height * ratio).toInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
}
