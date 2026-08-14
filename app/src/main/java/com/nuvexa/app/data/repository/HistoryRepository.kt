package com.nuvexa.app.data.repository

import com.nuvexa.app.data.local.dao.HistoryDao
import com.nuvexa.app.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao,
) {
    fun observeAll(): Flow<List<HistoryEntity>> = historyDao.observeAll()

    suspend fun record(toolId: String, summary: String) {
        historyDao.insert(HistoryEntity(toolId = toolId, summary = summary, timestampEpochMillis = System.currentTimeMillis()))
    }

    suspend fun delete(id: Long) = historyDao.deleteById(id)

    suspend fun clearAll() = historyDao.deleteAll()
}
