package com.nuvexa.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = IndigoPrimaryLight,
    onPrimary = IndigoOnPrimaryLight,
    primaryContainer = IndigoPrimaryContainerLight,
    onPrimaryContainer = IndigoOnPrimaryContainerLight,
    secondary = VioletSecondaryLight,
    onSecondary = VioletOnSecondaryLight,
    secondaryContainer = VioletSecondaryContainerLight,
    onSecondaryContainer = VioletOnSecondaryContainerLight,
    tertiary = SkyTertiaryLight,
    onTertiary = SkyOnTertiaryLight,
    tertiaryContainer = SkyTertiaryContainerLight,
    onTertiaryContainer = SkyOnTertiaryContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    error = StatusError,
    errorContainer = StatusErrorContainer,
    onError = SurfaceLight,
)

private val DarkColors = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = IndigoOnPrimaryDark,
    primaryContainer = IndigoPrimaryContainerDark,
    onPrimaryContainer = IndigoOnPrimaryContainerDark,
    secondary = VioletSecondaryDark,
    onSecondary = VioletOnSecondaryDark,
    secondaryContainer = VioletSecondaryContainerDark,
    onSecondaryContainer = VioletOnSecondaryContainerDark,
    tertiary = SkyTertiaryDark,
    onTertiary = SkyOnTertiaryDark,
    tertiaryContainer = SkyTertiaryContainerDark,
    onTertiaryContainer = SkyOnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    error = StatusErrorDark,
    errorContainer = StatusError,
    onError = BackgroundDark,
)

data class NuvexaStatusColors(
    val success: androidx.compose.ui.graphics.Color,
    val successContainer: androidx.compose.ui.graphics.Color,
    val warning: androidx.compose.ui.graphics.Color,
    val warningContainer: androidx.compose.ui.graphics.Color,
    val info: androidx.compose.ui.graphics.Color,
    val infoContainer: androidx.compose.ui.graphics.Color,
)

private val LightStatus = NuvexaStatusColors(
    success = StatusSuccess,
    successContainer = StatusSuccessContainer,
    warning = StatusWarning,
    warningContainer = StatusWarningContainer,
    info = StatusInfo,
    infoContainer = StatusInfoContainer,
)

private val DarkStatus = NuvexaStatusColors(
    success = StatusSuccessDark,
    successContainer = StatusSuccess,
    warning = StatusWarningDark,
    warningContainer = StatusWarning,
    info = StatusInfoDark,
    infoContainer = StatusInfo,
)

val LocalNuvexaStatusColors = androidx.compose.runtime.staticCompositionLocalOf { LightStatus }

@Composable
fun NuvexaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val statusColors = if (darkTheme) DarkStatus else LightStatus

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalSpacing provides NuvexaSpacing(),
        LocalNuvexaStatusColors provides statusColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NuvexaTypography,
            shapes = NuvexaShapes,
            content = content,
        )
    }
}
