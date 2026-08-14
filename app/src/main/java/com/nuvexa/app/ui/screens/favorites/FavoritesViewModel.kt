package com.nuvexa.app.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvexa.app.core.model.Tool
import com.nuvexa.app.core.registry.ToolRegistry
import com.nuvexa.app.data.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    val favoriteTools: StateFlow<List<Tool>> = favoritesRepository.observeFavoriteIds()
        .map { ids -> ids.mapNotNull(ToolRegistry::findById) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun moveUp(toolId: String) = reorder(toolId, -1)
    fun moveDown(toolId: String) = reorder(toolId, 1)

    private fun reorder(toolId: String, delta: Int) {
        val current = favoriteTools.value.map { it.id }.toMutableList()
        val index = current.indexOf(toolId)
        val target = index + delta
        if (index < 0 || target < 0 || target >= current.size) return
        current.add(target, current.removeAt(index))
        viewModelScope.launch { favoritesRepository.reorder(current) }
    }

    fun removeFavorite(toolId: String) {
        viewModelScope.launch { favoritesRepository.toggleFavorite(toolId) }
    }
}
