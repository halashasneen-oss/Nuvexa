package com.nuvexa.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class CalculatorMathTest {

    @Test
    fun `dividing by zero returns null instead of throwing`() {
        assertNull(BigDecimal("100").safeDivide(BigDecimal.ZERO))
        assertNull(BigDecimal.ZERO.safeDivide(BigDecimal.ZERO))
        assertNull(BigDecimal("-50").safeDivide(BigDecimal("0.00")))
    }

    @Test
    fun `non-terminating decimal results do not throw (the profit-margin regression)`() {
        // This is exactly the shape of the profit-margin bug: -700 / 3 has no exact,
        // terminating decimal representation, so the bare `/` operator throws
        // ArithmeticException here. Revenue of 3 is a completely ordinary value to enter.
        val result = BigDecimal("-700").safeDivide(BigDecimal("3"))
        assertEquals(BigDecimal("-233.33333333333333333"), result)
    }

    @Test
    fun `exact terminating division still matches plain division`() {
        assertEquals(BigDecimal("2.5"), BigDecimal("10").safeDivide(BigDecimal("4")))
    }

    @Test
    fun `negative divisor is handled without crashing`() {
        val result = BigDecimal("10").safeDivide(BigDecimal("-3"))
        assertEquals(BigDecimal("-3.3333333333333333333"), result)
    }
}
