package com.nuvexa.app

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class NuvexaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Ad init only reaches the network for optional ad serving; every core tool works
        // fully offline regardless of whether this succeeds.
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { MobileAds.initialize(this@NuvexaApp) }
        }
    }
}
