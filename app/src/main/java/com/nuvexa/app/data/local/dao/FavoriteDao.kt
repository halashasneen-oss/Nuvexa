package com.nuvexa.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nuvexa.app.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT toolId FROM favorites")
    suspend fun allToolIds(): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE toolId = :toolId)")
    fun observeIsFavorite(toolId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Update
    suspend fun updateAll(favorites: List<FavoriteEntity>)

    @Query("DELETE FROM favorites WHERE toolId = :toolId")
    suspend fun deleteByToolId(toolId: String)

    @Query("SELECT COALESCE(MAX(sortOrder), -1) FROM favorites")
    suspend fun maxSortOrder(): Int
}
