package com.nuvexa.app.data.repository

import com.nuvexa.app.data.local.dao.CurrencyRateDao
import com.nuvexa.app.data.local.entity.CurrencyRateEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** Manual, offline exchange rates only — Nuvexa never fetches live rates. */
@Singleton
class CurrencyRepository @Inject constructor(
    private val currencyRateDao: CurrencyRateDao,
) {
    fun observeAll(): Flow<List<CurrencyRateEntity>> = currencyRateDao.observeAll()

    suspend fun setRate(code: String, baseCode: String, rate: Double) {
        currencyRateDao.upsert(CurrencyRateEntity(code, baseCode, rate))
    }

    suspend fun toggleFavorite(entity: CurrencyRateEntity) {
        currencyRateDao.upsert(entity.copy(isFavorite = !entity.isFavorite))
    }

    suspend fun seedDefaultsIfEmpty() {
        if (currencyRateDao.count() > 0) return
        currencyRateDao.insertAllIfAbsent(DefaultRates.map { (code, rate) -> CurrencyRateEntity(code, "USD", rate) })
    }

    companion object {
        // Approximate starting points only — users are expected to edit these to match
        // real, current rates. Nuvexa never claims these are live.
        private val DefaultRates = listOf(
            "USD" to 1.0,
            "EUR" to 0.92,
            "GBP" to 0.79,
            "JPY" to 149.0,
            "SAR" to 3.75,
            "AED" to 3.67,
            "EGP" to 48.0,
            "CAD" to 1.36,
            "AUD" to 1.51,
            "INR" to 83.3,
        )
    }
}
