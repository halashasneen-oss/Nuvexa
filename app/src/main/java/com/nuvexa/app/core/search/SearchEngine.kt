package com.nuvexa.app.core.search

import android.content.Context
import com.nuvexa.app.core.model.Tool
import com.nuvexa.app.core.registry.ToolRegistry
import java.text.Normalizer
import java.util.Locale

/**
 * Local, offline, multilingual search over [ToolRegistry]. Matches the query against each
 * tool's localized name, description, category, and keyword aliases (natural phrases like
 * "how old am I" or "convertir imagen a pdf" baked into the string resources), so results
 * work the same in English, Arabic, French, and Spanish without any network call.
 */
object SearchEngine {

    private val stopWords = setOf(
        // English
        "a", "an", "the", "to", "of", "in", "on", "is", "my", "how", "do", "i", "does", "can", "you", "for", "am",
        // Arabic
        "من", "إلى", "في", "على", "هو", "هي", "أنا", "كيف", "هل", "ما", "و",
        // French
        "le", "la", "les", "de", "à", "un", "une", "du", "des", "en", "est", "comment", "je",
        // Spanish
        "el", "los", "las", "una", "es", "como", "yo", "mi",
    )

    fun search(context: Context, query: String, tools: List<Tool> = ToolRegistry.tools): List<Tool> {
        val trimmed = query.trim()
        if (trimmed.length < 2) return emptyList()

        val rawWords = normalize(trimmed).split(Regex("\\s+")).filter { it.isNotBlank() }
        val words = rawWords.filterNot { it in stopWords }.ifEmpty { rawWords }
        if (words.isEmpty()) return emptyList()

        return tools
            .map { tool -> tool to scoreTool(context, tool, words) }
            .filter { (_, result) -> result.matchedWords * 2 >= words.size } // at least ~60% of words matched, rounded up
            .sortedByDescending { (_, result) -> result.score }
            .map { it.first }
    }

    private data class ScoreResult(val score: Int, val matchedWords: Int)

    private fun scoreTool(context: Context, tool: Tool, words: List<String>): ScoreResult {
        val name = normalize(context.getString(tool.nameRes))
        val description = normalize(context.getString(tool.descriptionRes))
        val keywords = tool.keywordsRes?.let { normalize(context.getString(it)) } ?: ""
        val category = normalize(context.getString(tool.category.nameRes))

        var score = 0
        var matchedWords = 0

        for (word in words) {
            val hitScore = when {
                name == word -> 100
                name.startsWith(word) -> 70
                name.contains(word) -> 45
                keywords.contains(word) -> 30
                description.contains(word) -> 18
                category.contains(word) -> 8
                else -> 0
            }
            if (hitScore > 0) {
                matchedWords++
                score += hitScore
            }
        }
        return ScoreResult(score, matchedWords)
    }

    private fun normalize(text: String): String {
        val lower = text.lowercase(Locale.getDefault())
        val decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD)
        return decomposed.replace(Regex("\\p{Mn}+"), "")
    }
}
