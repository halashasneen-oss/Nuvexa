package com.nuvexa.app.ui.tools.color

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.util.copyTextToClipboard
import com.nuvexa.app.core.util.hexToRgb
import com.nuvexa.app.core.util.rgbToHex
import com.nuvexa.app.core.util.rgbToHsl
import com.nuvexa.app.ui.components.SecondaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import kotlin.random.Random

@Composable
fun ColorConverterScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var hex by remember { mutableStateOf("4F46E5") }

    val rgb = hexToRgb(hex)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        OutlinedTextField(
            value = hex,
            onValueChange = { hex = it.uppercase().filter { c -> c.isLetterOrDigit() }.take(6) },
            label = { Text("HEX") },
            leadingIcon = { Text("#", modifier = Modifier.padding(start = spacing.s)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = NuvexaExtraType.monospaceBody,
        )

        if (rgb != null) {
            val (r, g, b) = rgb
            val hsl = rgbToHsl(r, g, b)
            val composeColor = Color(r, g, b)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(composeColor),
            )

            ColorValueRow(label = "HEX", value = rgbToHex(r, g, b)) { context.copyTextToClipboard(rgbToHex(r, g, b)) }
            ColorValueRow(label = "RGB", value = "rgb($r, $g, $b)") { context.copyTextToClipboard("rgb($r, $g, $b)") }
            ColorValueRow(label = "HSL", value = "hsl(${hsl.h}, ${hsl.s}%, ${hsl.l}%)") {
                context.copyTextToClipboard("hsl(${hsl.h}, ${hsl.s}%, ${hsl.l}%)")
            }
        }

        SecondaryButton(
            text = stringResource(R.string.action_generate),
            onClick = { hex = "%06X".format(Random.nextInt(0xFFFFFF + 1)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ColorValueRow(label: String, value: String, onCopy: () -> Unit) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCopy() },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = NuvexaExtraType.monospaceBody)
    }
}
