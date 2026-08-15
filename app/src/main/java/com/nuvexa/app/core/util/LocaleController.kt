package com.nuvexa.app.core.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

private const val PREFS_NAME = "nuvexa_locale_prefs"
private const val KEY_LANGUAGE_TAG = "language_tag"

/**
 * Applies a per-app language override.
 *
 * MainActivity is a plain ComponentActivity (no AppCompatActivity), so it can't rely on
 * AppCompat's automatic base-context wrapping — MainActivity patches its own Configuration in
 * attachBaseContext() instead. That read has to be synchronous and race-free, which
 * AppCompatDelegate.getApplicationLocales() isn't guaranteed to be right after a matching
 * setApplicationLocales() call (the actual internal state update isn't necessarily complete by
 * the time a caller immediately turns around and reads it back, e.g. via Activity.recreate()).
 * So the tag actually used for rendering is stored directly in a plain, synchronously-committed
 * SharedPreferences entry; AppCompatDelegate is still told too, best-effort, so the OS-level
 * "App languages" system settings page (API 33+) stays in sync.
 */
object LocaleController {

    fun applyLanguage(context: Context, tag: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply {
                if (tag.isNullOrBlank()) remove(KEY_LANGUAGE_TAG) else putString(KEY_LANGUAGE_TAG, tag)
            }
            .commit() // synchronous — must be durable before the caller recreates the Activity

        val locales = if (tag.isNullOrBlank()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(tag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    /** The tag actually used to patch the Activity's Configuration — null means "follow system". */
    fun readStoredLanguageTag(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_LANGUAGE_TAG, null)

    /** Native endonyms — intentionally not translated, same convention every language picker uses. */
    val nativeLanguageNames: Map<String, String> = mapOf(
        "en" to "English",
        "ar" to "العربية",
        "fr" to "Français",
        "es" to "Español",
    )

    val supportedLanguageTags: List<String?> = listOf(null, "en", "ar", "fr", "es")
}
