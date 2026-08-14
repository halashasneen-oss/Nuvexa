package com.nuvexa.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Saves into this app's own external-files Pictures directory — no storage permission is
 * ever needed for this location on any supported Android version. The file is also
 * reachable through the system share sheet for the user to save elsewhere if they want it
 * in their gallery.
 */
fun Context.saveBitmapToAppPictures(bitmap: Bitmap, prefix: String): File {
    val dir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Nuvexa").apply { mkdirs() }
    val timestamp = SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.US).format(Date())
    val file = File(dir, "${prefix}_$timestamp.png")
    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
    return file
}

fun Context.saveJpegToAppPictures(bitmap: Bitmap, prefix: String, quality: Int): File {
    val dir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Nuvexa").apply { mkdirs() }
    val timestamp = SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.US).format(Date())
    val file = File(dir, "${prefix}_$timestamp.jpg")
    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out) }
    return file
}
