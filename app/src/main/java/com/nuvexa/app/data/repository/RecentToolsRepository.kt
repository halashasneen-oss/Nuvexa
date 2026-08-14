package com.nuvexa.app.data.repository

import com.nuvexa.app.data.local.dao.RecentToolDao
import com.nuvexa.app.data.local.entity.RecentToolEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentToolsRepository @Inject constructor(
    private val recentToolDao: RecentToolDao,
) {
    fun observeRecent(limit: Int = 12): Flow<List<RecentToolEntity>> = recentToolDao.observeRecent(limit)

    fun observeMostUsed(limit: Int = 6): Flow<List<RecentToolEntity>> = recentToolDao.observeMostUsed(limit)

    suspend fun recordUsage(toolId: String) {
        val existing = recentToolDao.find(toolId)
        recentToolDao.upsert(
            RecentToolEntity(
                toolId = toolId,
                lastUsedEpochMillis = System.currentTimeMillis(),
                useCount = (existing?.useCount ?: 0) + 1,
            ),
        )
    }

    suspend fun clearAll() = recentToolDao.deleteAll()
}
