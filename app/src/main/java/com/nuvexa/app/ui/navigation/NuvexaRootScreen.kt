package com.nuvexa.app.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.BannerAdView

private data class BottomDestination(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
)

private val bottomDestinations = listOf(
    BottomDestination(Routes.HOME, R.string.nav_home, Icons.Rounded.Home),
    BottomDestination(Routes.TOOLS, R.string.nav_tools, Icons.Rounded.GridView),
    BottomDestination(Routes.FAVORITES, R.string.nav_favorites, Icons.Rounded.Star),
    BottomDestination(Routes.HISTORY, R.string.nav_history, Icons.Rounded.History),
    BottomDestination(Routes.SETTINGS, R.string.nav_settings, Icons.Rounded.Settings),
)

@Composable
fun NuvexaRootScreen(startWithOnboarding: Boolean) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomDestinations.any { it.route == currentRoute }

    val glowHeightPx = with(LocalDensity.current) { 560.dp.toPx() }
    val scheme = MaterialTheme.colorScheme
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            scheme.primaryContainer.copy(alpha = 0.44f),
            scheme.tertiaryContainer.copy(alpha = 0.20f),
            scheme.background,
        ),
        startY = 0f,
        endY = glowHeightPx,
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    Column {
                        // Ad placement/behavior is intentionally unchanged.
                        BannerAdView(modifier = Modifier.padding(vertical = 4.dp))
                        Surface(
                            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                            color = scheme.surface.copy(alpha = 0.97f),
                            shadowElevation = 10.dp,
                            border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.22f)),
                        ) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                tonalElevation = 0.dp,
                            ) {
                                bottomDestinations.forEach { destination ->
                                    val selected = currentRoute == destination.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { navController.navigateToBottomDestination(destination.route) },
                                        icon = {
                                            Icon(
                                                imageVector = destination.icon,
                                                contentDescription = null,
                                            )
                                        },
                                        label = { Text(stringResource(destination.labelRes)) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = scheme.onPrimaryContainer,
                                            selectedTextColor = scheme.onSurface,
                                            indicatorColor = scheme.primaryContainer,
                                            unselectedIconColor = scheme.onSurfaceVariant,
                                            unselectedTextColor = scheme.onSurfaceVariant,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = Color.Transparent,
                contentColor = scheme.onBackground,
            ) {
                NuvexaNavHost(
                    navController = navController,
                    startWithOnboarding = startWithOnboarding,
                    modifier = Modifier,
                )
            }
        }
    }
}
