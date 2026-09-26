package com.nuvexa.app.core.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receives Android's chooser callback after the user selects a destination for sharing Nuvexa.
 * The receiver is non-exported in the manifest and is addressed explicitly by PendingIntent.
 */
class RewardShareReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_SHARE_TARGET_CHOSEN) {
            AdFreeSessionManager.completeShareAndGrant(context)
        }
    }

    companion object {
        const val ACTION_SHARE_TARGET_CHOSEN =
            "com.nuvexa.app.action.REWARD_SHARE_TARGET_CHOSEN"
    }
}
