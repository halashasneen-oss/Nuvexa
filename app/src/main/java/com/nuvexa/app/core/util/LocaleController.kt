package com.nuvexa.app.core.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/** Applies a per-app language override via AppCompat's locale API (works back to API 26). */
object LocaleController {
    fun applyLanguage(tag: String?) {
        val locales = if (tag.isNullOrBlank()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(tag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    /** Native endonyms — intentionally not translated, same convention every language picker uses. */
    val nativeLanguageNames: Map<String, String> = mapOf(
        "en" to "English",
        "ar" to "العربية",
        "fr" to "Français",
        "es" to "Español",
    )

    val supportedLanguageTags: List<String?> = listOf(null, "en", "ar", "fr", "es")
}
