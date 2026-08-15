package com.nuvexa.app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvexa.app.core.util.LocaleController
import com.nuvexa.app.data.repository.ThemeMode
import com.nuvexa.app.ui.navigation.NuvexaRootScreen
import com.nuvexa.app.ui.theme.NuvexaTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    // MainActivity is a plain ComponentActivity (not AppCompatActivity), so AppCompat's usual
    // automatic per-app-language base-context wrapping never runs for it. Apply the persisted
    // locale tag ourselves on every (re)creation — including cold start, where no stored tag
    // correctly falls through to the system language unchanged. Reads LocaleController's own
    // synchronously-committed storage rather than AppCompatDelegate.getApplicationLocales(),
    // which isn't guaranteed to be immediately consistent right after a matching
    // setApplicationLocales() call from the same user action.
    override fun attachBaseContext(newBase: Context) {
        val tag = LocaleController.readStoredLanguageTag(newBase)
        val patched = if (!tag.isNullOrBlank()) {
            val configuration = Configuration(newBase.resources.configuration)
            configuration.setLocale(Locale.forLanguageTag(tag))
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
