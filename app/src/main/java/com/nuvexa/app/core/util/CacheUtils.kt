package com.nuvexa.app.core.util

import android.content.Context

/** Deletes everything under the app's cache directory — the temporary files tools create
 * while processing images and other output before it's saved or shared. */
fun Context.clearNuvexaCache() {
    cacheDir?.listFiles()?.forEach { it.deleteRecursively() }
}
