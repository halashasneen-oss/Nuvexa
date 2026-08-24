package com.nuvexa.app.core.util

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

private val SAFE_DIVIDE_CONTEXT = MathContext(20, RoundingMode.HALF_UP)

/**
 * Safe replacement for the bare `/` operator on [BigDecimal]. The plain operator throws
 * `ArithmeticException` in two easy-to-hit cases: dividing by zero, and dividing by any
 * value that doesn't produce an exact, terminating decimal (e.g. `1 / 3` — not just exotic
 * inputs, completely ordinary ones). Returns `null` for a zero divisor instead of crashing;
 * otherwise rounds to 20 significant digits, which is more than enough precision for any
 * calculator result Nuvexa displays.
 */
fun BigDecimal.safeDivide(divisor: BigDecimal): BigDecimal? {
    if (divisor.signum() == 0) return null
    return this.divide(divisor, SAFE_DIVIDE_CONTEXT)
}
