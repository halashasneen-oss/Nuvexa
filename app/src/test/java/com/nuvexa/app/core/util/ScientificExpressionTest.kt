package com.nuvexa.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ScientificExpressionTest {
    @Test
    fun respectsOperatorPrecedence() {
        assertEquals(14.0, ScientificExpression.evaluate("2 + 3 * 4"), 1e-9)
        assertEquals(20.0, ScientificExpression.evaluate("(2 + 3) * 4"), 1e-9)
    }

    @Test
    fun exponentIsRightAssociativeAndSupportsUnaryMinus() {
        assertEquals(512.0, ScientificExpression.evaluate("2^3^2"), 1e-9)
        assertEquals(-4.0, ScientificExpression.evaluate("-2^2"), 1e-9)
        assertEquals(0.25, ScientificExpression.evaluate("2^-2"), 1e-9)
    }

    @Test
    fun supportsScientificFunctionsAndDegreeMode() {
        assertEquals(1.0, ScientificExpression.evaluate("sin(90)", useDegrees = true), 1e-9)
        assertEquals(3.0, ScientificExpression.evaluate("sqrt(9)"), 1e-9)
        assertEquals(2.0, ScientificExpression.evaluate("log(100)"), 1e-9)
        assertEquals(Math.PI, ScientificExpression.evaluate("pi"), 1e-9)
    }

    @Test
    fun acceptsLocalizedNumbers() {
        assertEquals(3.5, ScientificExpression.evaluate("1,5 + 2"), 1e-9)
        assertEquals(3.5, ScientificExpression.evaluate("١٫٥ + ٢"), 1e-9)
        assertEquals(3.5, ScientificExpression.evaluate("۱٫۵ + ۲"), 1e-9)
    }

    @Test
    fun rejectsInvalidOrUnsafeMath() {
        assertThrows(IllegalArgumentException::class.java) { ScientificExpression.evaluate("") }
        assertThrows(IllegalArgumentException::class.java) { ScientificExpression.evaluate("10 / 0") }
        assertThrows(IllegalArgumentException::class.java) { ScientificExpression.evaluate("sqrt(-1)") }
    }
}
