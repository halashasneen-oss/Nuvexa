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
 * Keeps one interstitial ready in memory and only shows it at a natural transition: when the
 * user leaves a tool. To avoid interrupting a utility-heavy session too often, an ad is eligible
 * only once every three tool exits. If an ad is unavailable, navigation continues immediately.
 */
object InterstitialAdManager {
    private const val EXITS_BETWEEN_ADS = 3

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var exitsSinceLastAd = 0

    fun preload(context: Context) {
        if (interstitialAd != null || isLoading) return

        AdsInitializer.ensureInitialized(context)
        isLoading = true
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

    fun showOnToolExit(activity: Activity, onContinue: () -> Unit) {
        exitsSinceLastAd += 1

        if (exitsSinceLastAd < EXITS_BETWEEN_ADS) {
            preload(activity)
            onContinue()
            return
        }

        val ad = interstitialAd
        if (ad == null) {
            preload(activity)
            onContinue()
            return
        }

        exitsSinceLastAd = 0
        interstitialAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                preload(activity)
                onContinue()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                preload(activity)
                onContinue()
            }
        }
        ad.show(activity)
    }
}
