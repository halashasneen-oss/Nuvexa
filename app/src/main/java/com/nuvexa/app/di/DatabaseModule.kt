package com.nuvexa.app.di

import android.content.Context
import androidx.room.Room
import com.nuvexa.app.data.local.dao.CurrencyRateDao
import com.nuvexa.app.data.local.dao.FavoriteDao
import com.nuvexa.app.data.local.dao.HistoryDao
import com.nuvexa.app.data.local.dao.RecentToolDao
import com.nuvexa.app.data.local.db.NuvexaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NuvexaDatabase =
        Room.databaseBuilder(context, NuvexaDatabase::class.java, NuvexaDatabase.DATABASE_NAME).build()

    @Provides
    fun provideFavoriteDao(db: NuvexaDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideHistoryDao(db: NuvexaDatabase): HistoryDao = db.historyDao()

    @Provides
    fun provideRecentToolDao(db: NuvexaDatabase): RecentToolDao = db.recentToolDao()

    @Provides
    fun provideCurrencyRateDao(db: NuvexaDatabase): CurrencyRateDao = db.currencyRateDao()
}
