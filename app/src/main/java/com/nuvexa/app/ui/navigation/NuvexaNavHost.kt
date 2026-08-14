package com.nuvexa.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nuvexa.app.core.registry.ToolRegistry
import com.nuvexa.app.ui.screens.favorites.FavoritesScreen
import com.nuvexa.app.ui.screens.history.HistoryScreen
import com.nuvexa.app.ui.screens.home.HomeScreen
import com.nuvexa.app.ui.screens.onboarding.OnboardingScreen
import com.nuvexa.app.ui.screens.settings.SettingsScreen
import com.nuvexa.app.ui.screens.tools.CategoryToolsScreen
import com.nuvexa.app.ui.screens.tools.ToolsScreen
import com.nuvexa.app.ui.tools.ToolScreenHost

@Composable
fun NuvexaNavHost(
    navController: NavHostController,
    startWithOnboarding: Boolean,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = if (startWithOnboarding) Routes.ONBOARDING else Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onOpenTool = { toolId -> navController.navigateToTool(toolId) },
                onOpenCategory = { categoryId -> navController.navigate(Routes.toolsCategory(categoryId)) },
                onSeeAllRecent = { navController.navigate(Routes.HISTORY) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }

        composable(Routes.TOOLS) {
            ToolsScreen(
                onOpenTool = { toolId -> navController.navigateToTool(toolId) },
                onOpenCategory = { categoryId -> navController.navigate(Routes.toolsCategory(categoryId)) },
            )
        }

        composable(
            route = Routes.TOOLS_CATEGORY,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId").orEmpty()
            CategoryToolsScreen(
                categoryId = categoryId,
                onOpenTool = { toolId -> navController.navigateToTool(toolId) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(onOpenTool = { toolId -> navController.navigateToTool(toolId) })
        }

        composable(Routes.HISTORY) {
            HistoryScreen(onOpenTool = { toolId -> navController.navigateToTool(toolId) })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen()
        }

        composable(
            route = Routes.TOOL_DETAIL,
            arguments = listOf(navArgument("toolId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val toolId = backStackEntry.arguments?.getString("toolId").orEmpty()
            val tool = ToolRegistry.findById(toolId)
            if (tool != null) {
                ToolScreenHost(tool = tool, onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun NavController.navigateToTool(toolId: String) {
    navigate(Routes.toolDetail(toolId))
}
