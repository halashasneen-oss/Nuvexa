package com.nuvexa.app.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvexa.app.data.repository.FavoritesRepository
import com.nuvexa.app.data.repository.HistoryRepository
import com.nuvexa.app.data.repository.RecentToolsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Shared chrome logic for every tool screen: favorite toggling, recent-usage tracking,
 * and history recording. One instance is created per tool-detail nav back stack entry.
 */
@HiltViewModel
class ToolChromeViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val recentToolsRepository: RecentToolsRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel() {

    private var boundToolId: String? = null

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun bind(toolId: String) {
        if (boundToolId == toolId) return
        boundToolId = toolId
        viewModelScope.launch { recentToolsRepository.recordUsage(toolId) }
        viewModelScope.launch {
            favoritesRepository.observeIsFavorite(toolId).collect { _isFavorite.value = it }
        }
    }

    fun toggleFavorite() {
        val id = boundToolId ?: return
        viewModelScope.launch { favoritesRepository.toggleFavorite(id) }
    }

    fun recordHistory(summary: String) {
        val id = boundToolId ?: return
        viewModelScope.launch { historyRepository.record(id, summary) }
    }
}
