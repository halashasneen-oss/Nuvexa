package com.nuvexa.app.core.util

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists the temporary ad-free reward locally so it survives process death and app restarts.
 * No account, analytics identifier, or server is involved.
 */
object AdFreeSessionManager {
    private const val PREFS_NAME = "nuvexa_ad_rewards"
    private const val KEY_AD_FREE_UNTIL = "ad_free_until_epoch_ms"
    private const val KEY_PENDING_SHARE = "pending_reward_share"

    const val REWARD_DURATION_MILLIS = 60L * 60L * 1000L

    private val _adFreeUntil = MutableStateFlow(0L)
    val adFreeUntil: StateFlow<Long> = _adFreeUntil.asStateFlow()

    private val _pendingShare = MutableStateFlow(false)
    val pendingShare: StateFlow<Boolean> = _pendingShare.asStateFlow()

    fun initialize(context: Context) {
        val prefs = prefs(context)
        val until = prefs.getLong(KEY_AD_FREE_UNTIL, 0L)
        _adFreeUntil.value = if (until > System.currentTimeMillis()) until else 0L
        _pendingShare.value = prefs.getBoolean(KEY_PENDING_SHARE, false)
        if (until != 0L && until <= System.currentTimeMillis()) {
            prefs.edit().remove(KEY_AD_FREE_UNTIL).apply()
        }
    }

    fun isAdFree(context: Context): Boolean {
        val until = prefs(context).getLong(KEY_AD_FREE_UNTIL, 0L)
        val active = until > System.currentTimeMillis()
        if (!active && until != 0L) {
            prefs(context).edit().remove(KEY_AD_FREE_UNTIL).apply()
            _adFreeUntil.value = 0L
        }
        return active
    }

    fun markSharePending(context: Context) {
        prefs(context).edit().putBoolean(KEY_PENDING_SHARE, true).apply()
        _pendingShare.value = true
    }

    fun hasPendingShare(context: Context): Boolean =
        prefs(context).getBoolean(KEY_PENDING_SHARE, false)

    /**
     * Called only after Android's chooser reports that the user selected a share target.
     * Selecting a target is the strongest platform signal available; apps cannot reliably
     * verify that a third-party service ultimately published the content.
     */
    fun completeShareAndGrant(context: Context) {
        if (!hasPendingShare(context)) return

        val until = System.currentTimeMillis() + REWARD_DURATION_MILLIS
        prefs(context).edit()
            .putLong(KEY_AD_FREE_UNTIL, until)
            .putBoolean(KEY_PENDING_SHARE, false)
            .apply()

        _pendingShare.value = false
        _adFreeUntil.value = until
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
