package com.nuvexa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvexa.app.data.repository.ThemeMode
import com.nuvexa.app.ui.navigation.NuvexaRootScreen
import com.nuvexa.app.ui.theme.NuvexaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.onboardingComplete.value == null }

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemDark
            }

            NuvexaTheme(darkTheme = darkTheme) {
                val onboardingComplete by viewModel.onboardingComplete.collectAsStateWithLifecycle()
                onboardingComplete?.let { complete ->
                    NuvexaRootScreen(startWithOnboarding = !complete)
                }
            }
        }
    }
}
