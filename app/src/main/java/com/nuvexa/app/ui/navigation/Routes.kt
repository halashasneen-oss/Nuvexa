package com.nuvexa.app.ui.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val TOOLS = "tools"
    const val TOOLS_CATEGORY = "tools/{categoryId}"
    const val FAVORITES = "favorites"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val TOOL_DETAIL = "tool/{toolId}"

    fun toolsCategory(categoryId: String) = "tools/$categoryId"
    fun toolDetail(toolId: String) = "tool/$toolId"
}
