package com.nuvexa.app.core.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.MobileAds

/**
 * Initializes Google Mobile Ads exactly once and runs queued ad-load callbacks only after
 * initialization has completed. This removes the race where a banner/interstitial could try
 * to load before the SDK was ready.
 */
object AdsInitializer {
    private val lock = Any()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var initialized = false
    private var initializing = false
    private val pendingCallbacks = mutableListOf<() -> Unit>()

    fun ensureInitialized(context: Context, onReady: (() -> Unit)? = null) {
        var shouldStart = false
        var runImmediately = false

        synchronized(lock) {
            if (initialized) {
                runImmediately = onReady != null
            } else {
                onReady?.let(pendingCallbacks::add)
                if (!initializing) {
                    initializing = true
                    shouldStart = true
                }
            }
        }

        if (runImmediately) {
            mainHandler.post { onReady?.invoke() }
            return
        }

        if (!shouldStart) return

        MobileAds.initialize(context.applicationContext) {
            val callbacks = synchronized(lock) {
                initialized = true
                initializing = false
                pendingCallbacks.toList().also { pendingCallbacks.clear() }
            }
            callbacks.forEach { callback -> mainHandler.post(callback) }
        }
    }
}
