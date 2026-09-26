package com.nuvexa.app

import android.app.Application
import com.nuvexa.app.core.util.AdFreeSessionManager
import com.nuvexa.app.core.util.InterstitialAdManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NuvexaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Restore any active reward before ad managers decide whether they should preload.
        AdFreeSessionManager.initialize(this)

        // Existing interstitial behavior remains unchanged outside an active ad-free reward.
        InterstitialAdManager.preload(this)
    }
}
