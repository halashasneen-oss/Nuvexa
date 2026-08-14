package com.nuvexa.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.nuvexa.app.data.local.datastore.SettingsKeys
import com.nuvexa.app.data.local.datastore.settingsDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class StartScreen { HOME, TOOLS, FAVORITES }

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val themeMode = context.settingsDataStore.data.map { prefs ->
        prefs[SettingsKeys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
    }

    val languageTag = context.settingsDataStore.data.map { prefs -> prefs[SettingsKeys.LANGUAGE_TAG] }

    val startScreen = context.settingsDataStore.data.map { prefs ->
        prefs[SettingsKeys.START_SCREEN]?.let { runCatching { StartScreen.valueOf(it) }.getOrNull() } ?: StartScreen.HOME
    }

    val hapticsEnabled = context.settingsDataStore.data.map { prefs -> prefs[SettingsKeys.HAPTICS_ENABLED] ?: true }

    val reduceMotion = context.settingsDataStore.data.map { prefs -> prefs[SettingsKeys.REDUCE_MOTION] ?: false }

    val onboardingComplete = context.settingsDataStore.data.map { prefs -> prefs[SettingsKeys.ONBOARDING_COMPLETE] ?: false }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[SettingsKeys.THEME_MODE] = mode.name }
    }

    suspend fun setLanguageTag(tag: String?) {
        context.settingsDataStore.edit {
            if (tag == null) it.remove(SettingsKeys.LANGUAGE_TAG) else it[SettingsKeys.LANGUAGE_TAG] = tag
        }
    }

    suspend fun setStartScreen(screen: StartScreen) {
        context.settingsDataStore.edit { it[SettingsKeys.START_SCREEN] = screen.name }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setReduceMotion(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.REDUCE_MOTION] = enabled }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.ONBOARDING_COMPLETE] = complete }
    }
}
