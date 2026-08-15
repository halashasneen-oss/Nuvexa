package com.nuvexa.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvexa.app.data.repository.HistoryRepository
import com.nuvexa.app.data.repository.RecentToolsRepository
import com.nuvexa.app.data.repository.SettingsRepository
import com.nuvexa.app.data.repository.StartScreen
import com.nuvexa.app.data.repository.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val languageTag: String? = null,
    val startScreen: StartScreen = StartScreen.HOME,
    val hapticsEnabled: Boolean = true,
    val reduceMotion: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository,
    private val recentToolsRepository: RecentToolsRepository,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.themeMode,
        settingsRepository.languageTag,
        settingsRepository.startScreen,
        settingsRepository.hapticsEnabled,
        settingsRepository.reduceMotion,
    ) { theme, language, start, haptics, reduceMotion ->
        SettingsUiState(theme, language, start, haptics, reduceMotion)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { settingsRepository.setThemeMode(mode) }

    fun setLanguage(tag: String?) = viewModelScope.launch { settingsRepository.setLanguageTag(tag) }

    fun setStartScreen(screen: StartScreen) = viewModelScope.launch { settingsRepository.setStartScreen(screen) }

    fun setHapticsEnabled(enabled: Boolean) = viewModelScope.launch { settingsRepository.setHapticsEnabled(enabled) }

    fun setReduceMotion(enabled: Boolean) = viewModelScope.launch { settingsRepository.setReduceMotion(enabled) }

    fun clearHistory() = viewModelScope.launch { historyRepository.clearAll() }

    fun clearRecent() = viewModelScope.launch { recentToolsRepository.clearAll() }
}
