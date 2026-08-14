package com.nuvexa.app.data.local.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context

val Context.settingsDataStore by preferencesDataStore(name = "nuvexa_settings")

object SettingsKeys {
    val THEME_MODE: Preferences.Key<String> = stringPreferencesKey("theme_mode")
    val LANGUAGE_TAG: Preferences.Key<String> = stringPreferencesKey("language_tag")
    val START_SCREEN: Preferences.Key<String> = stringPreferencesKey("start_screen")
    val HAPTICS_ENABLED: Preferences.Key<Boolean> = booleanPreferencesKey("haptics_enabled")
    val REDUCE_MOTION: Preferences.Key<Boolean> = booleanPreferencesKey("reduce_motion")
    val ONBOARDING_COMPLETE: Preferences.Key<Boolean> = booleanPreferencesKey("onboarding_complete")
}
