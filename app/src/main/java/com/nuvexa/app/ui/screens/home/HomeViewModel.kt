package com.nuvexa.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvexa.app.core.registry.ToolRegistry
import com.nuvexa.app.core.search.Recommendations
import com.nuvexa.app.data.local.entity.RecentToolEntity
import com.nuvexa.app.data.repository.FavoritesRepository
import com.nuvexa.app.data.repository.RecentToolsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val favoriteToolIds: List<String> = emptyList(),
    val recentTools: List<RecentToolEntity> = emptyList(),
    val recommendedToolIds: List<String> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    favoritesRepository: FavoritesRepository,
    recentToolsRepository: RecentToolsRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        favoritesRepository.observeFavoriteIds(),
        recentToolsRepository.observeRecent(limit = 8),
        recentToolsRepository.observeMostUsed(limit = 6),
    ) { favoriteIds, recent, mostUsed ->
        HomeUiState(
            favoriteToolIds = favoriteIds,
            recentTools = recent,
            recommendedToolIds = Recommendations.recommend(mostUsed).map { it.id },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun toolById(id: String) = ToolRegistry.findById(id)
}
