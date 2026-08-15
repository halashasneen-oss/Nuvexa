package com.nuvexa.app.ui.screens.settings

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvexa.app.R
import com.nuvexa.app.core.util.LocaleController
import com.nuvexa.app.core.util.clearNuvexaCache
import com.nuvexa.app.data.repository.StartScreen
import com.nuvexa.app.data.repository.ThemeMode
import com.nuvexa.app.ui.components.ConfirmDialog
import com.nuvexa.app.ui.theme.LocalSpacing
import android.content.Intent
import android.net.Uri
import android.widget.Toast

private sealed interface SettingsRow {
    data class Section(val title: String) : SettingsRow
    data class Radio(val label: String, val selected: Boolean, val onSelect: () -> Unit) : SettingsRow
    data class Switch(val label: String, val description: String?, val checked: Boolean, val onToggle: (Boolean) -> Unit) : SettingsRow
    data class Click(val label: String, val trailing: String?, val onClick: (() -> Unit)? = null) : SettingsRow
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showClearCacheDone by remember { mutableStateOf(false) }
    var showClearHistoryConfirm by remember { mutableStateOf(false) }
    var showClearRecentConfirm by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    // Every label is resolved here, in composable scope, before being handed to the plain
    // (non-composable) buildList block below — stringResource() can't be called from there.
    val sectionAppearance = stringResource(R.string.settings_section_appearance)
    val themeLight = stringResource(R.string.settings_theme_light)
    val themeDark = stringResource(R.string.settings_theme_dark)
    val themeSystem = stringResource(R.string.settings_theme_system)
    val sectionLanguage = stringResource(R.string.settings_section_language)
    val languageSystemLabel = stringResource(R.string.settings_language_system)
    val sectionBehavior = stringResource(R.string.settings_section_behavior)
    val navHome = stringResource(R.string.nav_home)
    val navTools = stringResource(R.string.nav_tools)
    val navFavorites = stringResource(R.string.nav_favorites)
    val haptics = stringResource(R.string.settings_haptics)
    val reduceMotionLabel = stringResource(R.string.settings_reduce_motion)
    val sectionStorage = stringResource(R.string.settings_section_storage)
    val clearCache = stringResource(R.string.settings_clear_cache)
    val sectionPrivacy = stringResource(R.string.settings_section_privacy)
    val clearHistoryLabel = stringResource(R.string.settings_clear_history)
    val clearRecentLabel = stringResource(R.string.settings_clear_recent)
    val sectionAbout = stringResource(R.string.settings_section_about)
    val versionLabel = stringResource(R.string.settings_version)
    val privacyPolicyLabel = stringResource(R.string.settings_privacy_policy)
    val termsLabel = stringResource(R.string.settings_terms)
    val contactLabel = stringResource(R.string.settings_contact)
    val supportEmail = stringResource(R.string.support_email)

    val rows = buildList<SettingsRow> {
        add(SettingsRow.Section(sectionAppearance))
        add(SettingsRow.Radio(themeLight, uiState.themeMode == ThemeMode.LIGHT) { viewModel.setThemeMode(ThemeMode.LIGHT) })
        add(SettingsRow.Radio(themeDark, uiState.themeMode == ThemeMode.DARK) { viewModel.setThemeMode(ThemeMode.DARK) })
        add(SettingsRow.Radio(themeSystem, uiState.themeMode == ThemeMode.SYSTEM) { viewModel.setThemeMode(ThemeMode.SYSTEM) })

        add(SettingsRow.Section(sectionLanguage))
        LocaleController.supportedLanguageTags.forEach { tag ->
            val label = tag?.let { LocaleController.nativeLanguageNames[it] } ?: languageSystemLabel
            add(
                SettingsRow.Radio(label, uiState.languageTag == tag) {
                    viewModel.setLanguage(tag)
                    // Commits the tag synchronously (see LocaleController) and tells
                    // AppCompatDelegate too, then recreates this Activity so
                    // attachBaseContext() picks the new tag up immediately.
                    LocaleController.applyLanguage(context, tag)
                    (context as? Activity)?.recreate()
                },
            )
        }

        add(SettingsRow.Section(sectionBehavior))
        add(SettingsRow.Radio(navHome, uiState.startScreen == StartScreen.HOME) { viewModel.setStartScreen(StartScreen.HOME) })
        add(SettingsRow.Radio(navTools, uiState.startScreen == StartScreen.TOOLS) { viewModel.setStartScreen(StartScreen.TOOLS) })
        add(SettingsRow.Radio(navFavorites, uiState.startScreen == StartScreen.FAVORITES) { viewModel.setStartScreen(StartScreen.FAVORITES) })
        add(SettingsRow.Switch(haptics, null, uiState.hapticsEnabled) { viewModel.setHapticsEnabled(it) })
        add(SettingsRow.Switch(reduceMotionLabel, null, uiState.reduceMotion) { viewModel.setReduceMotion(it) })

        add(SettingsRow.Section(sectionStorage))
        add(
            SettingsRow.Click(clearCache, null) {
                context.clearNuvexaCache()
                showClearCacheDone = true
            },
        )

        add(SettingsRow.Section(sectionPrivacy))
        add(SettingsRow.Click(clearHistoryLabel, null) { showClearHistoryConfirm = true })
        add(SettingsRow.Click(clearRecentLabel, null) { showClearRecentConfirm = true })

        add(SettingsRow.Section(sectionAbout))
        add(SettingsRow.Click(versionLabel, com.nuvexa.app.BuildConfig.VERSION_NAME))
        add(SettingsRow.Click(privacyPolicyLabel, null) { showPrivacyDialog = true })
        add(SettingsRow.Click(termsLabel, null) { showTermsDialog = true })
        add(
            SettingsRow.Click(contactLabel, null) {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$supportEmail"))
                runCatching { context.startActivity(intent) }
            },
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.l, vertical = spacing.m),
        )
        Text(
            text = stringResource(R.string.settings_privacy_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = spacing.l),
        )
        LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = spacing.m)) {
            items(rows) { row ->
                when (row) {
                    is SettingsRow.Section -> Text(
                        text = row.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = spacing.l, vertical = spacing.s),
                    )
                    is SettingsRow.Radio -> Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = row.onSelect)
                            .padding(horizontal = spacing.l, vertical = spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = row.selected, onClick = row.onSelect)
                        Text(row.label, modifier = Modifier.padding(start = spacing.s))
                    }
                    is SettingsRow.Switch -> Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.l, vertical = spacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(row.label)
                            row.description?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(checked = row.checked, onCheckedChange = row.onToggle)
                    }
                    is SettingsRow.Click -> Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (row.onClick != null) Modifier.clickable(onClick = row.onClick) else Modifier)
                            .padding(horizontal = spacing.l, vertical = spacing.m),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(row.label)
                        row.trailing?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }
            }
        }
    }

    if (showClearCacheDone) {
        val message = stringResource(R.string.settings_clear_cache_done)
        androidx.compose.runtime.LaunchedEffect(showClearCacheDone) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            showClearCacheDone = false
        }
    }

    if (showClearHistoryConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.history_clear_all_title),
            body = stringResource(R.string.history_clear_all_body),
            onConfirm = { viewModel.clearHistory(); showClearHistoryConfirm = false },
            onDismiss = { showClearHistoryConfirm = false },
        )
    }

    if (showClearRecentConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.settings_clear_recent),
            body = stringResource(R.string.history_clear_all_body),
            onConfirm = { viewModel.clearRecent(); showClearRecentConfirm = false },
            onDismiss = { showClearRecentConfirm = false },
        )
    }

    if (showPrivacyDialog) {
        InfoDialog(
            title = stringResource(R.string.settings_privacy_policy),
            body = stringResource(R.string.privacy_policy_body),
            onDismiss = { showPrivacyDialog = false },
        )
    }

    if (showTermsDialog) {
        InfoDialog(
            title = stringResource(R.string.settings_terms),
            body = stringResource(R.string.terms_body),
            onDismiss = { showTermsDialog = false },
        )
    }
}

@Composable
private fun InfoDialog(title: String, body: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) }
        },
    )
}
