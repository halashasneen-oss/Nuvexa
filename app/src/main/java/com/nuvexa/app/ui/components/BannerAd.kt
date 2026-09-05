package com.nuvexa.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.nuvexa.app.BuildConfig
import com.nuvexa.app.core.util.AdsInitializer

/**
 * A banner ad for non-critical screens only (never mid-workflow). Failure to load — no
 * network, no fill, Play Services missing — simply leaves an empty view; it never crashes
 * or blocks the surrounding UI. Debug builds use Google's public test unit; release builds
 * use the production Nuvexa unit configured in Gradle.
 */
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = BuildConfig.BANNER_AD_UNIT_ID,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { AdsInitializer.ensureInitialized(context) }
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                runCatching { loadAd(AdRequest.Builder().build()) }
            }
        },
    )
}
