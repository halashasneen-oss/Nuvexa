package com.nuvexa.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.nuvexa.app.BuildConfig
import com.nuvexa.app.core.util.AdFreeSessionManager
import com.nuvexa.app.core.util.AdsInitializer
import kotlinx.coroutines.delay

/**
 * Banner ad used on non-critical top-level screens. The view is kept stable across recomposition,
 * waits for Mobile Ads initialization before requesting an ad, and is destroyed with the
 * composable to avoid leaking the Activity.
 *
 * During an earned ad-free hour the composable leaves the hierarchy entirely, so no banner is
 * visible and the existing banner resumes automatically when the reward expires.
 */
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = BuildConfig.BANNER_AD_UNIT_ID,
) {
    val context = LocalContext.current
    val adFreeUntil by AdFreeSessionManager.adFreeUntil.collectAsState()
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(adFreeUntil) {
        now = System.currentTimeMillis()
        val remaining = adFreeUntil - now
        if (remaining > 0L) {
            delay(remaining)
            now = System.currentTimeMillis()
        }
    }

    if (adFreeUntil > now || AdFreeSessionManager.isAdFree(context)) return

    val adView = remember(context, adUnitId) {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId
        }
    }

    LaunchedEffect(adView, adUnitId) {
        AdsInitializer.ensureInitialized(context) {
            adView.post {
                runCatching { adView.loadAd(AdRequest.Builder().build()) }
            }
        }
    }

    DisposableEffect(adView) {
        onDispose { adView.destroy() }
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { adView },
    )
}
