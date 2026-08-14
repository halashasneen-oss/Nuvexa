package com.nuvexa.app.ui.tools.device

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.core.util.formatBytes
import com.nuvexa.app.ui.theme.LocalSpacing

private data class InfoRow(val label: String, val value: String)

@Composable
fun DeviceInfoScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val notAvailable = stringResource(R.string.device_info_not_available)
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val rows = remember(configuration) {
        buildDeviceInfoRows(context, notAvailable, configuration.screenWidthDp, configuration.screenHeightDp, density.density)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.s)) {
        rows.forEachIndexed { index, row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(row.label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(row.value, style = MaterialTheme.typography.bodyMedium)
            }
            if (index != rows.lastIndex) HorizontalDivider()
        }
    }
}

private fun buildDeviceInfoRows(
    context: Context,
    notAvailable: String,
    screenWidthDp: Int,
    screenHeightDp: Int,
    density: Float,
): List<InfoRow> {
    val rows = mutableListOf<InfoRow>()

    rows += InfoRow(context.getString(R.string.device_info_model), "${Build.MANUFACTURER} ${Build.MODEL}".trim().ifBlank { notAvailable })
    rows += InfoRow(context.getString(R.string.device_info_manufacturer), Build.MANUFACTURER ?: notAvailable)
    rows += InfoRow(context.getString(R.string.device_info_android_version), Build.VERSION.RELEASE ?: notAvailable)
    rows += InfoRow(context.getString(R.string.device_info_api_level), Build.VERSION.SDK_INT.toString())
    rows += InfoRow(context.getString(R.string.device_info_screen), "${screenWidthDp}×${screenHeightDp} dp  (${"%.1f".format(density)}x)")
    rows += InfoRow(context.getString(R.string.device_info_cpu_cores), Runtime.getRuntime().availableProcessors().toString())

    runCatching {
        val stat = StatFs(Environment.getDataDirectory().path)
        val total = stat.blockCountLong * stat.blockSizeLong
        val free = stat.availableBlocksLong * stat.blockSizeLong
        rows += InfoRow(context.getString(R.string.device_info_storage), "${formatBytes(total - free)} / ${formatBytes(total)}")
    }.onFailure { rows += InfoRow(context.getString(R.string.device_info_storage), notAvailable) }

    runCatching {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        rows += InfoRow(
            context.getString(R.string.device_info_ram),
            "${formatBytes(memoryInfo.totalMem - memoryInfo.availMem)} / ${formatBytes(memoryInfo.totalMem)}",
        )
    }.onFailure { rows += InfoRow(context.getString(R.string.device_info_ram), notAvailable) }

    runCatching {
        val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        if (level >= 0 && scale > 0) {
            rows += InfoRow(context.getString(R.string.device_info_battery), "${level * 100 / scale}%")
        } else {
            rows += InfoRow(context.getString(R.string.device_info_battery), notAvailable)
        }
    }.onFailure { rows += InfoRow(context.getString(R.string.device_info_battery), notAvailable) }

    return rows
}
