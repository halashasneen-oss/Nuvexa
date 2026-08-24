package com.nuvexa.app.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CodeFormattersTest {

    @Test
    fun `well-formed XML without a DOCTYPE is pretty-printed`() {
        val result = formatXml("<root><child>value</child></root>")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().contains("value"))
    }

    @Test
    fun `malformed XML fails gracefully instead of throwing`() {
        val result = formatXml("<root><unclosed></root>")
        assertTrue(result.isFailure)
    }

    @Test
    fun `unclosed tag with no closing root fails gracefully`() {
        val result = formatXml("<root><child>")
        assertTrue(result.isFailure)
    }

    @Test
    fun `blank input fails gracefully`() {
        val result = formatXml("")
        assertTrue(result.isFailure)
    }

    @Test
    fun `external general entity (classic XXE) is rejected, not resolved`() {
        val payload = """
            <?xml version="1.0"?>
            <!DOCTYPE foo [ <!ENTITY xxe SYSTEM "file:///etc/passwd"> ]>
            <foo>&xxe;</foo>
        """.trimIndent()
        val result = formatXml(payload)
        assertTrue(result.isFailure)
    }

    @Test
    fun `external parameter entity XXE variant is rejected`() {
        val payload = """
            <?xml version="1.0"?>
            <!DOCTYPE foo [ <!ENTITY % xxe SYSTEM "file:///etc/passwd"> %xxe; ]>
            <foo>bar</foo>
        """.trimIndent()
        val result = formatXml(payload)
        assertTrue(result.isFailure)
    }

    @Test
    fun `external DTD reference is rejected without a network fetch`() {
        val payload = """
            <?xml version="1.0"?>
            <!DOCTYPE foo SYSTEM "http://example.com/evil.dtd">
            <foo>bar</foo>
        """.trimIndent()
        val result = formatXml(payload)
        assertTrue(result.isFailure)
    }

    @Test
    fun `billion-laughs style internal entity expansion is rejected outright`() {
        val payload = """
            <?xml version="1.0"?>
            <!DOCTYPE lolz [
              <!ENTITY lol "lol">
              <!ENTITY lol2 "&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;">
              <!ENTITY lol3 "&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;">
            ]>
            <lolz>&lol3;</lolz>
        """.trimIndent()
        val result = formatXml(payload)
        // disallow-doctype-decl rejects the DOCTYPE before any entity is ever expanded.
        assertTrue(result.isFailure)
    }

    @Test
    fun `deeply nested XML does not crash, only succeeds or fails gracefully`() {
        val depth = 20_000
        val payload = buildString {
            append("<root>")
            repeat(depth) { append("<a>") }
            repeat(depth) { append("</a>") }
            append("</root>")
        }
        // The point of this test is that no exception/Error escapes formatXml, regardless
        // of whether formatting a document this deep ultimately succeeds or fails.
        val result = formatXml(payload)
        assertTrue(result.isSuccess || result.isFailure)
    }

    @Test
    fun `very large well-formed XML does not crash`() {
        val elementCount = 20_000
        val payload = buildString {
            append("<root>")
            repeat(elementCount) { i -> append("<item id=\"$i\">value$i</item>") }
            append("</root>")
        }
        val result = formatXml(payload)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().contains("value0"))
    }

    @Test
    fun `formatting never silently swallows failure as success`() {
        val result = formatXml("not xml at all }{")
        assertFalse(result.isSuccess)
    }
}
