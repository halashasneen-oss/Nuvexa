package com.nuvexa.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class LocalizedNumberInputTest {
    @Test
    fun normalizesArabicAndPersianDigits() {
        assertEquals("123.45", normalizeLocalizedNumberInput("١٢٣٫٤٥", true, false))
        assertEquals("678.9", normalizeLocalizedNumberInput("۶۷۸,۹", true, false))
    }

    @Test
    fun keepsOnlyOneDecimalSeparator() {
        assertEquals("12.34", normalizeLocalizedNumberInput("12,3.4", true, false))
    }

    @Test
    fun handlesLocalizedMinusAndIntegerMode() {
        assertEquals("-42.5", normalizeLocalizedNumberInput("−٤٢٫٥", true, true))
        assertEquals("425", normalizeLocalizedNumberInput("٤٢٫٥", false, false))
    }
}
