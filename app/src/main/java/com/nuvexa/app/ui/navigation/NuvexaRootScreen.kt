package com.nuvexa.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val bottomDestinations = listOf(
    BottomDestination(Routes.HOME, R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    BottomDestination(Routes.TOOLS, R.string.nav_tools, Icons.Filled.GridView, Icons.Outlined.GridView),
    BottomDestination(Routes.FAVORITES, R.string.nav_favorites, Icons.Filled.Star, Icons.Outlined.StarBorder),
    BottomDestination(Routes.HISTORY, R.string.nav_history, Icons.Filled.History, Icons.Outlined.History),
    BottomDestination(Routes.SETTINGS, R.string.nav_settings, Icons.Filled.Settings, Icons.Outlined.Settings),
)

@Composable
fun NuvexaRootScreen(startWithOnboarding: Boolean) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomDestinations.any { it.route == currentRoute }

    val glowHeightPx = with(LocalDensity.current) { 520.dp.toPx() }
    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.045f),
            backgroundColor,
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
                        // Ad placement/frequency is intentionally preserved.
                        BannerAdView(modifier = Modifier.padding(vertical = 4.dp))
                        Surface(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 3.dp,
                            shadowElevation = 5.dp,
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
                                            androidx.compose.material3.Icon(
                                                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                                contentDescription = null,
                                            )
                                        },
                                        label = { Text(stringResource(destination.labelRes)) },
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
                contentColor = onBackgroundColor,
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
