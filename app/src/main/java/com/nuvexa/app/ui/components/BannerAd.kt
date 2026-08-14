package com.nuvexa.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/** Google's official public test banner unit ID — replace with a real one before release. */
const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

/**
 * A banner ad for non-critical screens only (never mid-workflow). Failure to load — no
 * network, no fill, Play Services missing — simply leaves an empty view; it never crashes
 * or blocks the surrounding UI.
 */
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = TEST_BANNER_AD_UNIT_ID,
) {
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
