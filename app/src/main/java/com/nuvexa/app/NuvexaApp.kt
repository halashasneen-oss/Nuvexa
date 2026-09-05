package com.nuvexa.app

import android.app.Application
import com.nuvexa.app.core.util.InterstitialAdManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NuvexaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Begin AdMob initialization and preload one interstitial early so it is ready at the
        // first eligible natural transition out of a tool.
        InterstitialAdManager.preload(this)
    }
}
