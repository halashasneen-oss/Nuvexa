package com.nuvexa.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nuvexa.app.data.local.entity.RecentToolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentToolDao {
    @Query("SELECT * FROM recent_tools ORDER BY lastUsedEpochMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int = 12): Flow<List<RecentToolEntity>>

    @Query("SELECT * FROM recent_tools ORDER BY useCount DESC LIMIT :limit")
    fun observeMostUsed(limit: Int = 6): Flow<List<RecentToolEntity>>

    @Query("SELECT * FROM recent_tools WHERE toolId = :toolId")
    suspend fun find(toolId: String): RecentToolEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentToolEntity)

    @Query("DELETE FROM recent_tools")
    suspend fun deleteAll()
}
