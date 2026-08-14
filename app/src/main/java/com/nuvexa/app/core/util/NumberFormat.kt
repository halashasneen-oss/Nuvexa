package com.nuvexa.app.core.util

import java.math.BigDecimal
import java.math.RoundingMode

/** Formats a numeric result for display: no trailing zeros, no scientific notation. */
fun formatResultNumber(value: BigDecimal, maxFractionDigits: Int = 8): String {
    val rounded = value.setScale(maxFractionDigits, RoundingMode.HALF_UP).stripTrailingZeros()
    val plain = rounded.toPlainString()
    return if (plain == "-0") "0" else plain
}

fun formatResultNumber(value: Double, maxFractionDigits: Int = 6): String {
    if (value.isNaN() || value.isInfinite()) return "0"
    return formatResultNumber(BigDecimal.valueOf(value), maxFractionDigits)
}
