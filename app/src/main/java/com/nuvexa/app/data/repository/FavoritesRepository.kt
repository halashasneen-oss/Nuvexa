package com.nuvexa.app.data.repository

import com.nuvexa.app.data.local.dao.FavoriteDao
import com.nuvexa.app.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
) {
    fun observeFavoriteIds(): Flow<List<String>> =
        favoriteDao.observeAll().map { list -> list.map { it.toolId } }

    fun observeIsFavorite(toolId: String): Flow<Boolean> = favoriteDao.observeIsFavorite(toolId)

    suspend fun toggleFavorite(toolId: String) {
        val current = favoriteDao.allToolIds()
        if (toolId in current) {
            favoriteDao.deleteByToolId(toolId)
        } else {
            val nextOrder = favoriteDao.maxSortOrder() + 1
            favoriteDao.insert(FavoriteEntity(toolId, nextOrder, System.currentTimeMillis()))
        }
    }

    suspend fun reorder(orderedToolIds: List<String>) {
        val updated = orderedToolIds.mapIndexed { index, id ->
            FavoriteEntity(id, index, System.currentTimeMillis())
        }
        favoriteDao.updateAll(updated)
    }
}
