package com.nuvexa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val toolId: String,
    val sortOrder: Int,
    val addedAtEpochMillis: Long,
)
