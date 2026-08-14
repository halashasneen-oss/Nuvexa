package com.nuvexa.app.core.util

import kotlin.math.ln
import kotlin.math.pow

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (ln(bytes.toDouble()) / ln(1024.0)).toInt().coerceIn(0, units.lastIndex)
    val value = bytes / 1024.0.pow(digitGroups)
    return "%.1f %s".format(value, units[digitGroups])
}
