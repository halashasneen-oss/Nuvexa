package com.nuvexa.app.core.util

import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

fun formatXml(input: String): Result<String> = runCatching {
    val factory = DocumentBuilderFactory.newInstance().apply {
        isNamespaceAware = true
        setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    }
    val document = factory.newDocumentBuilder().parse(InputSource(StringReader(input)))
    document.normalize()
    stripWhitespaceNodes(document)

    val transformer = TransformerFactory.newInstance().newTransformer().apply {
        setOutputProperty(OutputKeys.INDENT, "yes")
        setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
        setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
    }
    val writer = StringWriter()
    transformer.transform(DOMSource(document), StreamResult(writer))
    writer.toString().trim()
}

private fun stripWhitespaceNodes(node: Node) {
    val children = node.childNodes
    var i = children.length - 1
    while (i >= 0) {
        val child = children.item(i)
        if (child.nodeType == Node.TEXT_NODE && child.textContent.isBlank()) {
            node.removeChild(child)
        } else {
            stripWhitespaceNodes(child)
        }
        i--
    }
}

/** A lightweight, best-effort HTML indenter — not a full parser, but enough to make
 * hand-written markup readable. */
fun formatHtml(input: String): String {
    val voidTags = setOf("br", "hr", "img", "input", "meta", "link", "area", "base", "col", "embed", "param", "source", "track", "wbr")
    val tagRegex = Regex("<[^>]+>")
    var depth = 0
    val builder = StringBuilder()
    var lastIndex = 0

    for (match in tagRegex.findAll(input)) {
        val textBefore = input.substring(lastIndex, match.range.first).trim()
        if (textBefore.isNotEmpty()) {
            builder.append("  ".repeat(depth)).append(textBefore).append('\n')
        }
        val tag = match.value
        val tagName = Regex("</?([a-zA-Z0-9]+)").find(tag)?.groupValues?.get(1)?.lowercase()
        val isClosing = tag.startsWith("</")
        val isSelfClosing = tag.endsWith("/>") || (tagName != null && tagName in voidTags)

        if (isClosing) depth = (depth - 1).coerceAtLeast(0)
        builder.append("  ".repeat(depth)).append(tag).append('\n')
        if (!isClosing && !isSelfClosing) depth++

        lastIndex = match.range.last + 1
    }
    val remaining = input.substring(lastIndex).trim()
    if (remaining.isNotEmpty()) builder.append("  ".repeat(depth)).append(remaining).append('\n')

    return builder.toString().trim()
}

/** A lightweight, best-effort CSS indenter based on brace nesting. */
fun formatCss(input: String): String {
    val builder = StringBuilder()
    var depth = 0
    val buffer = StringBuilder()

    fun flushBuffer() {
        val text = buffer.toString().trim()
        if (text.isNotEmpty()) builder.append("  ".repeat(depth)).append(text).append('\n')
        buffer.clear()
    }

    for (c in input) {
        when (c) {
            '{' -> {
                flushBuffer()
                builder.append("  ".repeat(depth)).append("{").append('\n')
                depth++
            }
            '}' -> {
                flushBuffer()
                depth = (depth - 1).coerceAtLeast(0)
                builder.append("  ".repeat(depth)).append("}").append('\n')
            }
            ';' -> {
                buffer.append(';')
                flushBuffer()
            }
            '\n', '\r' -> Unit
            else -> buffer.append(c)
        }
    }
    flushBuffer()
    return builder.toString().trim()
}
