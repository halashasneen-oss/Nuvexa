package com.nuvexa.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nuvexa.app.data.local.dao.CurrencyRateDao
import com.nuvexa.app.data.local.dao.FavoriteDao
import com.nuvexa.app.data.local.dao.HistoryDao
import com.nuvexa.app.data.local.dao.RecentToolDao
import com.nuvexa.app.data.local.entity.CurrencyRateEntity
import com.nuvexa.app.data.local.entity.FavoriteEntity
import com.nuvexa.app.data.local.entity.HistoryEntity
import com.nuvexa.app.data.local.entity.RecentToolEntity

@Database(
    entities = [
        FavoriteEntity::class,
        HistoryEntity::class,
        RecentToolEntity::class,
        CurrencyRateEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class NuvexaDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao
    abstract fun recentToolDao(): RecentToolDao
    abstract fun currencyRateDao(): CurrencyRateDao

    companion object {
        const val DATABASE_NAME = "nuvexa.db"
    }
}
