package com.nuvexa.app.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

private fun convert(category: UnitCategory, fromSymbol: String, toSymbol: String, value: Double): Double {
    val from = category.units.first { it.symbol == fromSymbol }
    val to = category.units.first { it.symbol == toSymbol }
    return to.fromBase(from.toBase(value))
}

class UnitConverterDataTest {

    @Test
    fun `one kilometer is one thousand meters`() {
        assertEquals(1000.0, convert(UnitCategory.LENGTH, "km", "m", 1.0), 0.0001)
    }

    @Test
    fun `one inch is 2point54 centimeters`() {
        assertEquals(2.54, convert(UnitCategory.LENGTH, "in", "cm", 1.0), 0.0001)
    }

    @Test
    fun `zero length converts to zero regardless of unit`() {
        assertEquals(0.0, convert(UnitCategory.LENGTH, "mi", "mm", 0.0), 0.0001)
    }

    @Test
    fun `negative length still converts linearly (e g temperature deltas use this too)`() {
        assertEquals(-1000.0, convert(UnitCategory.LENGTH, "km", "m", -1.0), 0.0001)
    }

    @Test
    fun `water freezes at zero celsius, 32 fahrenheit, and 273point15 kelvin`() {
        assertEquals(32.0, convert(UnitCategory.TEMPERATURE, "°C", "°F", 0.0), 0.0001)
        assertEquals(273.15, convert(UnitCategory.TEMPERATURE, "°C", "K", 0.0), 0.0001)
    }

    @Test
    fun `water boils at 100 celsius which is 212 fahrenheit`() {
        assertEquals(212.0, convert(UnitCategory.TEMPERATURE, "°C", "°F", 100.0), 0.0001)
    }

    @Test
    fun `negative temperatures convert correctly (fahrenheit below zero)`() {
        assertEquals(-40.0, convert(UnitCategory.TEMPERATURE, "°C", "°F", -40.0), 0.0001)
    }

    @Test
    fun `one kilogram is one thousand grams`() {
        assertEquals(1000.0, convert(UnitCategory.WEIGHT, "kg", "g", 1.0), 0.0001)
    }

    @Test
    fun `one gigabyte is 1024 megabytes (binary, not decimal)`() {
        assertEquals(1024.0, convert(UnitCategory.DATA, "GB", "MB", 1.0), 0.0001)
    }

    @Test
    fun `huge values convert without overflow or precision collapse`() {
        val result = convert(UnitCategory.LENGTH, "mm", "km", 1_000_000_000.0)
        assertEquals(1000.0, result, 0.01)
    }
}
