package com.nuvexa.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
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

    // MainActivity is a plain ComponentActivity (not AppCompatActivity), so AppCompat's usual
    // automatic per-app-language base-context wrapping never runs for it. Apply the persisted
    // locale (from AppCompatDelegate.setApplicationLocales) ourselves on every (re)creation —
    // including cold start, where an empty locale list correctly falls through to the system
    // language unchanged.
    override fun attachBaseContext(newBase: Context) {
        val locales = AppCompatDelegate.getApplicationLocales()
        val locale = if (!locales.isEmpty) locales[0] else null
        val patched = if (locale != null) {
            val configuration = android.content.res.Configuration(newBase.resources.configuration)
            configuration.setLocale(locale)
            newBase.createConfigurationContext(configuration)
        } else {
            newBase
        }
        super.attachBaseContext(patched)
    }

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
