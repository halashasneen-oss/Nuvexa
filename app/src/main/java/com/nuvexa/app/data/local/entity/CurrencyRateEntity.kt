package com.nuvexa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A user-entered exchange rate relative to 1 unit of [baseCode]. Manual only — never fetched live. */
@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey val code: String,
    val baseCode: String,
    val rate: Double,
    val isFavorite: Boolean = false,
)
