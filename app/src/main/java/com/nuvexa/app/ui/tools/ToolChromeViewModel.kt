package com.nuvexa.app.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvexa.app.data.repository.FavoritesRepository
import com.nuvexa.app.data.repository.HistoryRepository
import com.nuvexa.app.data.repository.RecentToolsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Shared chrome logic for every tool screen. */
@HiltViewModel
class ToolChromeViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val recentToolsRepository: RecentToolsRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel() {

    private var boundToolId: String? = null
    private var favoriteObservationJob: Job? = null

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun bind(toolId: String) {
        if (boundToolId == toolId) return
        boundToolId = toolId

        viewModelScope.launch { recentToolsRepository.recordUsage(toolId) }

        // A ViewModel can survive long enough to be rebound in navigation edge cases. Cancel the
        // old collector explicitly so two favorite flows can never race to update the same state.
        favoriteObservationJob?.cancel()
        favoriteObservationJob = viewModelScope.launch {
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
