package com.nuvexa.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ByteFormatTest {

    @Test
    fun `zero and negative sizes format as zero bytes instead of crashing`() {
        assertEquals("0 B", formatBytes(0))
        assertEquals("0 B", formatBytes(-100))
    }

    @Test
    fun `small byte counts stay in bytes`() {
        assertEquals("512.0 B", formatBytes(512))
    }

    @Test
    fun `kilobyte and megabyte boundaries convert correctly`() {
        assertEquals("1.0 KB", formatBytes(1024))
        assertEquals("1.0 MB", formatBytes(1024L * 1024))
        assertEquals("1.0 GB", formatBytes(1024L * 1024 * 1024))
    }
}
