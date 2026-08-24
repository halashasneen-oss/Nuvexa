package com.nuvexa.app.core.util

import android.content.Context
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/** Defers Google Mobile Ads SDK initialization until the moment a screen actually wants to
 * show an ad, instead of doing it unconditionally at app cold start. Most sessions in an
 * offline-first utility app never open an ad-showing screen at all, so this keeps startup
 * lean and avoids opening a network connection nobody asked for. Safe to call repeatedly —
 * only the first call does any work — and the ad network call itself still runs off the
 * main thread, exactly as it did when this lived in Application.onCreate(). */
object AdsInitializer {
    private val started = AtomicBoolean(false)

    fun ensureInitialized(context: Context) {
        if (!started.compareAndSet(false, true)) return
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { MobileAds.initialize(appContext) }
        }
    }
}
