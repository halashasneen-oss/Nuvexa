package com.nuvexa.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ColorUtilsTest {

    @Test
    fun `hex to rgb parses a standard color`() {
        assertEquals(Triple(79, 70, 229), hexToRgb("4F46E5"))
        assertEquals(Triple(79, 70, 229), hexToRgb("#4F46E5"))
    }

    @Test
    fun `hex to rgb rejects malformed input instead of crashing`() {
        assertNull(hexToRgb("not-a-color"))
        assertNull(hexToRgb("#ABC"))
        assertNull(hexToRgb(""))
    }

    @Test
    fun `rgb to hex round trips with hex to rgb`() {
        val (r, g, b) = Triple(255, 0, 128)
        val hex = rgbToHex(r, g, b)
        assertEquals(Triple(r, g, b), hexToRgb(hex))
    }

    @Test
    fun `black and white have zero saturation in HSL`() {
        assertEquals(HslValue(0, 0, 0), rgbToHsl(0, 0, 0))
        assertEquals(HslValue(0, 0, 100), rgbToHsl(255, 255, 255))
    }

    @Test
    fun `pure red converts to hue zero at full saturation and half lightness`() {
        val hsl = rgbToHsl(255, 0, 0)
        assertEquals(0, hsl.h)
        assertEquals(100, hsl.s)
        assertEquals(50, hsl.l)
    }

    @Test
    fun `hsl to rgb round trips back to the original rgb within rounding tolerance`() {
        val original = Triple(79, 70, 229)
        val hsl = rgbToHsl(original.first, original.second, original.third)
        val roundTripped = hslToRgb(hsl.h, hsl.s, hsl.l)
        val tolerance = 2
        assert(kotlin.math.abs(original.first - roundTripped.first) <= tolerance)
        assert(kotlin.math.abs(original.second - roundTripped.second) <= tolerance)
        assert(kotlin.math.abs(original.third - roundTripped.third) <= tolerance)
    }
}
