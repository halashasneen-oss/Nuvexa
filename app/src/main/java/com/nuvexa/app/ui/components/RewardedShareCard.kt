package com.nuvexa.app.ui.components

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.util.AdFreeSessionManager
import com.nuvexa.app.core.util.RewardShareReceiver
import com.nuvexa.app.core.util.RewardedAdManager
import com.nuvexa.app.core.util.RewardedAdState
import com.nuvexa.app.ui.theme.LocalSpacing
import kotlinx.coroutines.delay
import kotlin.math.ceil

private const val NUVEXA_PLAY_URL =
    "https://play.google.com/store/apps/details?id=com.nuvexa.app"

@Composable
fun RewardedShareCard(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val spacing = LocalSpacing.current
    val scheme = MaterialTheme.colorScheme

    val adFreeUntil by AdFreeSessionManager.adFreeUntil.collectAsState()
    val pendingShare by AdFreeSessionManager.pendingShare.collectAsState()
    val adState by RewardedAdManager.state.collectAsState()
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val isAdFree = adFreeUntil > now
    val remainingMinutes = if (isAdFree) {
        ceil((adFreeUntil - now) / 60_000.0).toInt().coerceAtLeast(1)
    } else {
        0
    }

    LaunchedEffect(adFreeUntil) {
        now = System.currentTimeMillis()
        while (adFreeUntil > now) {
            delay(30_000L)
            now = System.currentTimeMillis()
        }
        now = System.currentTimeMillis()
    }

    LaunchedEffect(isAdFree, pendingShare) {
        if (!isAdFree && !pendingShare) {
            RewardedAdManager.preload(context)
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = scheme.surface,
        contentColor = scheme.onSurface,
        border = BorderStroke(1.dp, scheme.primary.copy(alpha = 0.24f)),
        shadowElevation = 8.dp,
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    listOf(
                        scheme.primaryContainer.copy(alpha = 0.82f),
                        scheme.secondaryContainer.copy(alpha = 0.62f),
                        scheme.surface.copy(alpha = 0.95f),
                    ),
                ),
            ),
        ) {
            Column(
                modifier = Modifier.padding(spacing.l),
                verticalArrangement = Arrangement.spacedBy(spacing.m),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = scheme.primary.copy(alpha = 0.12f),
                        contentColor = scheme.primary,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = spacing.s, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = stringResource(R.string.reward_card_eyebrow),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = scheme.surface.copy(alpha = 0.72f),
                        contentColor = scheme.onSurface,
                    ) {
                        Text(
                            text = stringResource(R.string.reward_badge),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.m),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isAdFree) {
                            scheme.tertiaryContainer
                        } else {
                            scheme.primary.copy(alpha = 0.12f)
                        },
                        contentColor = if (isAdFree) {
                            scheme.onTertiaryContainer
                        } else {
                            scheme.primary
                        },
                    ) {
                        Icon(
                            imageVector = if (isAdFree) {
                                Icons.Rounded.VerifiedUser
                            } else {
                                Icons.Rounded.Share
                            },
                            contentDescription = null,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(28.dp),
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = when {
                                isAdFree -> stringResource(R.string.reward_active_title)
                                pendingShare -> stringResource(R.string.reward_pending_title)
                                else -> stringResource(R.string.reward_card_title)
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = scheme.onSurface,
                        )
                        Text(
                            text = when {
                                isAdFree -> stringResource(
                                    R.string.reward_active_body,
                                    remainingMinutes,
                                )
                                pendingShare -> stringResource(R.string.reward_pending_body)
                                else -> stringResource(R.string.reward_card_body)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                }

                if (!isAdFree) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Button(
                        onClick = {
                            if (pendingShare) {
                                launchShareChooser(context, activity)
                            } else if (activity != null) {
                                RewardedAdManager.show(
                                    activity = activity,
                                    onRewardEarned = {
                                        AdFreeSessionManager.markSharePending(context)
                                        launchShareChooser(context, activity)
                                    },
                                    onUnavailable = {
                                        RewardedAdManager.preload(context)
                                    },
                                )
                            }
                        },
                        enabled = pendingShare || (
                            activity != null &&
                                adState != RewardedAdState.LOADING &&
                                adState != RewardedAdState.SHOWING &&
                                adState != RewardedAdState.UNAVAILABLE
                            ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.primary,
                            contentColor = scheme.onPrimary,
                        ),
                    ) {
                        when {
                            adState == RewardedAdState.LOADING && !pendingShare -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = scheme.onPrimary,
                                )
                            }
                            pendingShare -> {
                                Icon(Icons.Rounded.Share, contentDescription = null)
                            }
                            else -> {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                            }
                        }
                        Text(
                            text = when {
                                pendingShare -> stringResource(R.string.reward_share_cta)
                                adState == RewardedAdState.LOADING ->
                                    stringResource(R.string.reward_loading)
                                else -> stringResource(R.string.reward_cta)
                            },
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }

                    if (adState == RewardedAdState.UNAVAILABLE) {
                        Text(
                            text = stringResource(R.string.reward_unavailable),
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = scheme.tertiary,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = stringResource(R.string.reward_active_note),
                            style = MaterialTheme.typography.labelLarge,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

private fun launchShareChooser(context: Context, activity: Activity?) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.reward_share_subject))
        putExtra(
            Intent.EXTRA_TEXT,
            context.getString(R.string.reward_share_text, NUVEXA_PLAY_URL),
        )
    }

    val callbackIntent = Intent(context, RewardShareReceiver::class.java).apply {
        action = RewardShareReceiver.ACTION_SHARE_TARGET_CHOSEN
    }
    val callback = PendingIntent.getBroadcast(
        context,
        913,
        callbackIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
    )

    val chooser = Intent.createChooser(
        shareIntent,
        context.getString(R.string.reward_share_chooser),
        callback.intentSender,
    )

    if (activity != null) {
        activity.startActivity(chooser)
    } else {
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}

private fun Context.findActivity(): Activity? {
    var current: Context = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        val base = current.baseContext
        if (base === current) return null
        current = base
    }
    return current as? Activity
}
