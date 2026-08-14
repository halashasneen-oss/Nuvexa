package com.nuvexa.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class NumberFormatTest {

    @Test
    fun `whole numbers drop trailing decimal zeros`() {
        assertEquals("5", formatResultNumber(BigDecimal("5.00000000")))
    }

    @Test
    fun `zero formats as plain zero, not negative zero`() {
        assertEquals("0", formatResultNumber(BigDecimal("-0.0000")))
        assertEquals("0", formatResultNumber(0.0))
    }

    @Test
    fun `negative values keep their sign`() {
        assertEquals("-42.5", formatResultNumber(BigDecimal("-42.5")))
    }

    @Test
    fun `large values are not rendered in scientific notation`() {
        assertEquals("123456789012", formatResultNumber(BigDecimal("123456789012")))
    }

    @Test
    fun `fraction digits are rounded, not truncated`() {
        assertEquals("1.23", formatResultNumber(BigDecimal("1.225"), maxFractionDigits = 2))
    }

    @Test
    fun `NaN and infinite doubles fall back to zero instead of crashing`() {
        assertEquals("0", formatResultNumber(Double.NaN))
        assertEquals("0", formatResultNumber(Double.POSITIVE_INFINITY))
        assertEquals("0", formatResultNumber(Double.NEGATIVE_INFINITY))
    }

    @Test
    fun `double formatting avoids binary floating point noise`() {
        // 0.1 + 0.2 famously isn't exactly 0.3 in binary floating point.
        assertEquals("0.3", formatResultNumber(0.1 + 0.2))
    }
}
