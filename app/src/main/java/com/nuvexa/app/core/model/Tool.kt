package com.nuvexa.app.core.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Describes one tool for navigation, browsing, favoriting, and search.
 * The actual screen UI lives in the `ui.tools` package and is looked up by [id]
 * from the tool detail nav route — this model never carries UI state.
 */
data class Tool(
    val id: String,
    val category: ToolCategory,
    val nameRes: Int,
    val descriptionRes: Int,
    val keywordsRes: Int? = null,
    val icon: ImageVector,
    val isNetworkDependent: Boolean = false,
)
