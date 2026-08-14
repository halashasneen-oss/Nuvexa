package com.nuvexa.app.ui.tools.color

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.core.util.hexToRgb
import com.nuvexa.app.core.util.hslToRgb
import com.nuvexa.app.core.util.rgbToHex
import com.nuvexa.app.core.util.rgbToHsl
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.SectionHeader
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import kotlin.random.Random

@Composable
fun PaletteGeneratorScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var baseHex by remember { mutableStateOf(randomHex()) }
    val (r, g, b) = hexToRgb(baseHex) ?: Triple(79, 70, 229)
    val hsl = rgbToHsl(r, g, b)

    val complementary = hslToRgb((hsl.h + 180) % 360, hsl.s, hsl.l)
    val analogous = listOf(-30, 0, 30).map { offset -> hslToRgb((hsl.h + offset + 360) % 360, hsl.s, hsl.l) }
    val randomPalette = remember(baseHex) { List(5) { hslToRgb(Random.nextInt(360), 55 + Random.nextInt(30), 45 + Random.nextInt(20)) } }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        BigSwatch(rgb = Triple(r, g, b), context = context)

        PrimaryButton(
            text = stringResource(R.string.action_generate),
            onClick = { baseHex = randomHex() },
            modifier = Modifier.fillMaxWidth(),
        )

        SectionHeader(title = stringResource(R.string.tool_color_palette_generator_name))
        SmallSwatch(complementary, context, Modifier.fillMaxWidth())

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s), modifier = Modifier.fillMaxWidth()) {
            analogous.forEach { color -> SmallSwatch(color, context, Modifier.weight(1f)) }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s), modifier = Modifier.fillMaxWidth()) {
            randomPalette.forEach { color -> SmallSwatch(color, context, Modifier.weight(1f)) }
        }
    }
}

private fun randomHex(): String = "%06X".format(Random.nextInt(0xFFFFFF + 1))

@Composable
private fun BigSwatch(rgb: Triple<Int, Int, Int>, context: Context) {
    val hex = rgbToHex(rgb.first, rgb.second, rgb.third)
    val isLight = rgbToHsl(rgb.first, rgb.second, rgb.third).l > 55
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2.4f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(rgb.first, rgb.second, rgb.third))
            .clickable { context.copyTextToClipboard(hex) },
        contentAlignment = Alignment.BottomCenter,
    ) {
        Text(
            hex,
            style = NuvexaExtraType.monospaceBody,
            color = if (isLight) Color.Black else Color.White,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Composable
private fun SmallSwatch(rgb: Triple<Int, Int, Int>, context: Context, modifier: Modifier = Modifier) {
    val hex = rgbToHex(rgb.first, rgb.second, rgb.third)
    Column(
        modifier = modifier.clickable { context.copyTextToClipboard(hex) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(rgb.first, rgb.second, rgb.third)),
        )
        Text(hex, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
    }
}
