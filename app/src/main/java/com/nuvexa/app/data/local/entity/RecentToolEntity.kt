package com.nuvexa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_tools")
data class RecentToolEntity(
    @PrimaryKey val toolId: String,
    val lastUsedEpochMillis: Long,
    val useCount: Int,
)
