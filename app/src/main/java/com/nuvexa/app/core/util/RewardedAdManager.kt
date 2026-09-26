package com.nuvexa.app.core.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.nuvexa.app.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RewardedAdState {
    IDLE,
    LOADING,
    READY,
    SHOWING,
    UNAVAILABLE,
}

/**
 * Owns exactly one opt-in rewarded ad. The reward callback is delayed until the full-screen ad
 * closes so the Android share chooser is never launched on top of the ad experience.
 */
object RewardedAdManager {
    private var rewardedAd: RewardedAd? = null

    private val _state = MutableStateFlow(RewardedAdState.IDLE)
    val state: StateFlow<RewardedAdState> = _state.asStateFlow()

    fun preload(context: Context) {
        if (AdFreeSessionManager.isAdFree(context)) return
        if (_state.value == RewardedAdState.LOADING ||
            _state.value == RewardedAdState.READY ||
            _state.value == RewardedAdState.SHOWING
        ) {
            return
        }

        val unitId = BuildConfig.REWARDED_AD_UNIT_ID
        if (unitId.isBlank()) {
            rewardedAd = null
            _state.value = RewardedAdState.UNAVAILABLE
            return
        }

        _state.value = RewardedAdState.LOADING
        AdsInitializer.ensureInitialized(context) {
            RewardedAd.load(
                context.applicationContext,
                unitId,
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        _state.value = RewardedAdState.READY
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                        _state.value = RewardedAdState.IDLE
                    }
                },
            )
        }
    }

    fun show(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onClosedWithoutReward: () -> Unit = {},
        onUnavailable: () -> Unit = {},
    ) {
        val ad = rewardedAd
        if (ad == null || _state.value != RewardedAdState.READY) {
            preload(activity)
            onUnavailable()
            return
        }

        rewardedAd = null
        _state.value = RewardedAdState.SHOWING
        var earnedReward = false

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                _state.value = RewardedAdState.IDLE
                if (earnedReward) {
                    onRewardEarned()
                } else {
                    onClosedWithoutReward()
                }
                preload(activity)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                _state.value = RewardedAdState.IDLE
                onUnavailable()
                preload(activity)
            }
        }

        ad.show(activity) {
            earnedReward = true
        }
    }
}
