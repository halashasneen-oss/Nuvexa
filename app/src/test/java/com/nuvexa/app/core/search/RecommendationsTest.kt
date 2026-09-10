package com.nuvexa.app.core.search

import com.nuvexa.app.core.registry.ToolRegistry
import com.nuvexa.app.data.local.entity.RecentToolEntity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Test

class RecommendationsTest {

    @Test
    fun fallbackNeverRecommendsAlreadyUsedTools() {
        val used = listOf(
            RecentToolEntity(toolId = "calc_basic", lastUsedEpochMillis = 1L, useCount = 10),
            RecentToolEntity(toolId = "text_analyzer", lastUsedEpochMillis = 2L, useCount = 8),
        )
        val usedIds = used.map { it.toolId }.toSet()

        val result = Recommendations.recommend(used, limit = ToolRegistry.tools.size)

        assertFalse(result.any { it.id in usedIds })
    }

    @Test
    fun registryIdsRemainUnique() {
        assertEquals(ToolRegistry.tools.size, ToolRegistry.tools.map { it.id }.toSet().size)
    }
}
