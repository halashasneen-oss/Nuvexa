package com.nuvexa.app.core.search

import com.nuvexa.app.core.model.Tool
import com.nuvexa.app.core.registry.ToolRegistry
import com.nuvexa.app.data.local.entity.RecentToolEntity

/**
 * A lightweight, fully local recommendation heuristic: it looks at which categories the
 * user actually uses (by usage count from [RecentToolEntity]) and surfaces other tools in
 * those same categories that the user hasn't tried yet. No cloud service, no ML model —
 * just usage counts already stored on-device.
 */
object Recommendations {

    fun recommend(mostUsed: List<RecentToolEntity>, limit: Int = 6): List<Tool> {
        if (mostUsed.isEmpty()) {
            return ToolRegistry.quickActionIds.mapNotNull(ToolRegistry::findById).take(limit)
        }

        val usedIds = mostUsed.map { it.toolId }.toSet()
        val favoredCategories = mostUsed
            .mapNotNull { ToolRegistry.findById(it.toolId)?.category }
            .distinct()

        val suggestions = favoredCategories
            .flatMap { category -> ToolRegistry.byCategory(category) }
            .filterNot { it.id in usedIds }
            .distinct()

        return if (suggestions.size >= limit) {
            suggestions.take(limit)
        } else {
            // Exclude both already-used tools and items already present in suggestions.
            // The old `&&` condition could let used tools reappear in the fallback list.
            val fallback = ToolRegistry.tools.filterNot { tool ->
                tool.id in usedIds || tool in suggestions
            }
            (suggestions + fallback).take(limit)
        }
    }
}
