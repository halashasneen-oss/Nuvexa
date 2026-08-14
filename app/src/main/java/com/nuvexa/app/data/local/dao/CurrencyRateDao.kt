package com.nuvexa.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nuvexa.app.data.local.entity.CurrencyRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyRateDao {
    @Query("SELECT * FROM currency_rates ORDER BY isFavorite DESC, code ASC")
    fun observeAll(): Flow<List<CurrencyRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rate: CurrencyRateEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIfAbsent(rates: List<CurrencyRateEntity>)

    @Query("SELECT COUNT(*) FROM currency_rates")
    suspend fun count(): Int
}
