package com.nuvexa.app.core.util

import kotlin.math.roundToInt

data class HslValue(val h: Int, val s: Int, val l: Int)

fun rgbToHsl(r: Int, g: Int, b: Int): HslValue {
    val rf = r / 255f
    val gf = g / 255f
    val bf = b / 255f
    val max = maxOf(rf, gf, bf)
    val min = minOf(rf, gf, bf)
    val l = (max + min) / 2f

    if (max == min) return HslValue(0, 0, (l * 100).roundToInt())

    val d = max - min
    val s = if (l > 0.5f) d / (2f - max - min) else d / (max + min)
    var h = when (max) {
        rf -> (gf - bf) / d + (if (gf < bf) 6f else 0f)
        gf -> (bf - rf) / d + 2f
        else -> (rf - gf) / d + 4f
    }
    h /= 6f
    return HslValue((h * 360).roundToInt(), (s * 100).roundToInt(), (l * 100).roundToInt())
}

fun hexToRgb(hex: String): Triple<Int, Int, Int>? {
    val cleaned = hex.removePrefix("#")
    if (cleaned.length != 6) return null
    return runCatching {
        val r = cleaned.substring(0, 2).toInt(16)
        val g = cleaned.substring(2, 4).toInt(16)
        val b = cleaned.substring(4, 6).toInt(16)
        Triple(r, g, b)
    }.getOrNull()
}

fun rgbToHex(r: Int, g: Int, b: Int): String = "#%02X%02X%02X".format(r, g, b)

fun hslToRgb(h: Int, s: Int, l: Int): Triple<Int, Int, Int> {
    val hf = ((h % 360) + 360) % 360 / 360f
    val sf = s / 100f
    val lf = l / 100f

    if (sf == 0f) {
        val v = (lf * 255).roundToInt()
        return Triple(v, v, v)
    }

    fun hueToRgb(p: Float, q: Float, tIn: Float): Float {
        var t = tIn
        if (t < 0f) t += 1f
        if (t > 1f) t -= 1f
        return when {
            t < 1f / 6f -> p + (q - p) * 6f * t
            t < 1f / 2f -> q
            t < 2f / 3f -> p + (q - p) * (2f / 3f - t) * 6f
            else -> p
        }
    }

    val q = if (lf < 0.5f) lf * (1 + sf) else lf + sf - lf * sf
    val p = 2 * lf - q
    val r = hueToRgb(p, q, hf + 1f / 3f)
    val g = hueToRgb(p, q, hf)
    val b = hueToRgb(p, q, hf - 1f / 3f)
    return Triple((r * 255).roundToInt(), (g * 255).roundToInt(), (b * 255).roundToInt())
}
