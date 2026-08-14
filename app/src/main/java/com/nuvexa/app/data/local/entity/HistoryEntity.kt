package com.nuvexa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A lightweight record of a completed tool operation — never the operation's actual
 * content (no file bytes, no clipboard text, no scanned results). [summary] is a short,
 * already-safe-to-display line such as "15% of 200 = 30".
 */
@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val toolId: String,
    val summary: String,
    val timestampEpochMillis: Long,
)
