package com.nuvexa.app.core.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nuvexa.app.BuildConfig

/**
 * Keeps one interstitial ready in memory and shows it only at a natural transition: leaving a
 * tool. Production shows at most once every three tool exits. Debug uses Google's test unit and
 * shows on every eligible exit so the integration can be verified without touching live ads.
 */
object InterstitialAdManager {
    private const val RELEASE_EXITS_BETWEEN_ADS = 3

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var isShowing = false
    private var exitsSinceLastAd = 0

    private val exitsBetweenAds: Int
        get() = if (BuildConfig.DEBUG) 1 else RELEASE_EXITS_BETWEEN_ADS

    fun preload(context: Context) {
        if (interstitialAd != null || isLoading || isShowing) return

        isLoading = true
        AdsInitializer.ensureInitialized(context) {
            InterstitialAd.load(
                context.applicationContext,
                BuildConfig.INTERSTITIAL_AD_UNIT_ID,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        isLoading = false
                        interstitialAd = ad
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        isLoading = false
                        interstitialAd = null
                    }
                },
            )
        }
    }

    fun showOnToolExit(activity: Activity, onContinue: () -> Unit) {
        if (isShowing) return

        exitsSinceLastAd += 1
        if (exitsSinceLastAd < exitsBetweenAds) {
            preload(activity)
            onContinue()
            return
        }

        val ad = interstitialAd
        if (ad == null) {
            // Keep the exit count eligible. As soon as a later preload succeeds, the next tool
            // exit can display it instead of silently resetting the frequency counter.
            preload(activity)
            onContinue()
            return
        }

        exitsSinceLastAd = 0
        interstitialAd = null
        isShowing = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isShowing = false
                preload(activity)
                onContinue()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                isShowing = false
                preload(activity)
                onContinue()
            }
        }
        ad.show(activity)
    }
}
