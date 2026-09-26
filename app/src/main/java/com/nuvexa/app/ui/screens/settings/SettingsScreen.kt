package com.nuvexa.app.ui.screens.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.nuvexa.app.ui.components.ContentSurface
import com.nuvexa.app.ui.theme.LocalSpacing

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

    val languageSystemLabel = stringResource(R.string.settings_language_system)
    val supportEmail = stringResource(R.string.support_email)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = spacing.l,
            end = spacing.l,
            top = spacing.l,
            bottom = spacing.xxl,
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.xl),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(R.string.settings_privacy_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            SettingsSection(
                title = stringResource(R.string.settings_section_appearance),
                icon = Icons.Rounded.Palette,
            ) {
                SettingsRadioRow(
                    label = stringResource(R.string.settings_theme_light),
                    selected = uiState.themeMode == ThemeMode.LIGHT,
                    onSelect = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                )
                SettingsDivider()
                SettingsRadioRow(
                    label = stringResource(R.string.settings_theme_dark),
                    selected = uiState.themeMode == ThemeMode.DARK,
                    onSelect = { viewModel.setThemeMode(ThemeMode.DARK) },
                )
                SettingsDivider()
                SettingsRadioRow(
                    label = stringResource(R.string.settings_theme_system),
                    selected = uiState.themeMode == ThemeMode.SYSTEM,
                    onSelect = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                )
            }
        }

        item {
            SettingsSection(
                title = stringResource(R.string.settings_section_language),
                icon = Icons.Rounded.Language,
            ) {
                LocaleController.supportedLanguageTags.forEachIndexed { index, tag ->
                    val label = tag?.let { LocaleController.nativeLanguageNames[it] }
                        ?: languageSystemLabel
                    SettingsRadioRow(
                        label = label,
                        selected = uiState.languageTag == tag,
                        onSelect = {
                            viewModel.setLanguage(tag)
                            LocaleController.applyLanguage(context, tag)
                            (context as? Activity)?.recreate()
                        },
                    )
                    if (index < LocaleController.supportedLanguageTags.lastIndex) {
                        SettingsDivider()
                    }
                }
            }
        }

        item {
            SettingsSection(
                title = stringResource(R.string.settings_section_behavior),
                icon = Icons.Rounded.Tune,
            ) {
                SettingsRadioRow(
                    label = stringResource(R.string.nav_home),
                    selected = uiState.startScreen == StartScreen.HOME,
                    onSelect = { viewModel.setStartScreen(StartScreen.HOME) },
                )
                SettingsDivider()
                SettingsRadioRow(
                    label = stringResource(R.string.nav_tools),
                    selected = uiState.startScreen == StartScreen.TOOLS,
                    onSelect = { viewModel.setStartScreen(StartScreen.TOOLS) },
                )
                SettingsDivider()
                SettingsRadioRow(
                    label = stringResource(R.string.nav_favorites),
                    selected = uiState.startScreen == StartScreen.FAVORITES,
                    onSelect = { viewModel.setStartScreen(StartScreen.FAVORITES) },
                )
                SettingsDivider()
                SettingsSwitchRow(
                    label = stringResource(R.string.settings_haptics),
                    checked = uiState.hapticsEnabled,
                    onToggle = viewModel::setHapticsEnabled,
                )
                SettingsDivider()
                SettingsSwitchRow(
                    label = stringResource(R.string.settings_reduce_motion),
                    checked = uiState.reduceMotion,
                    onToggle = viewModel::setReduceMotion,
                )
            }
        }

        item {
            SettingsSection(
                title = stringResource(R.string.settings_section_storage),
                icon = Icons.Rounded.Storage,
            ) {
                SettingsClickRow(
                    label = stringResource(R.string.settings_clear_cache),
                    onClick = {
                        context.clearNuvexaCache()
                        showClearCacheDone = true
                    },
                )
            }
        }

        item {
            SettingsSection(
                title = stringResource(R.string.settings_section_privacy),
                icon = Icons.Rounded.PrivacyTip,
            ) {
                SettingsClickRow(
                    label = stringResource(R.string.settings_clear_history),
                    onClick = { showClearHistoryConfirm = true },
                )
                SettingsDivider()
                SettingsClickRow(
                    label = stringResource(R.string.settings_clear_recent),
                    onClick = { showClearRecentConfirm = true },
                )
            }
        }

        item {
            SettingsSection(
                title = stringResource(R.string.settings_section_about),
                icon = Icons.Rounded.Info,
            ) {
                SettingsValueRow(
                    label = stringResource(R.string.settings_version),
                    value = com.nuvexa.app.BuildConfig.VERSION_NAME,
                )
                SettingsDivider()
                SettingsClickRow(
                    label = stringResource(R.string.settings_privacy_policy),
                    onClick = { showPrivacyDialog = true },
                )
                SettingsDivider()
                SettingsClickRow(
                    label = stringResource(R.string.settings_terms),
                    onClick = { showTermsDialog = true },
                )
                SettingsDivider()
                SettingsClickRow(
                    label = stringResource(R.string.settings_contact),
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_SENDTO,
                            Uri.parse("mailto:$supportEmail"),
                        )
                        runCatching { context.startActivity(intent) }
                    },
                )
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
            onConfirm = {
                viewModel.clearHistory()
                showClearHistoryConfirm = false
            },
            onDismiss = { showClearHistoryConfirm = false },
        )
    }

    if (showClearRecentConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.settings_clear_recent),
            body = stringResource(R.string.history_clear_all_body),
            onConfirm = {
                viewModel.clearRecent()
                showClearRecentConfirm = false
            },
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
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    val spacing = LocalSpacing.current
    val scheme = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = scheme.primary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = scheme.onBackground,
            )
        }

        ContentSurface(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = spacing.xs),
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsRadioRow(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(horizontal = spacing.m, vertical = spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = spacing.s),
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!checked) }
            .padding(horizontal = spacing.m, vertical = spacing.m),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

@Composable
private fun SettingsClickRow(
    label: String,
    onClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.m, vertical = spacing.m),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsValueRow(
    label: String,
    value: String,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.m, vertical = spacing.m),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
    )
}

@Composable
private fun InfoDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
            }
        },
    )
}
