package com.nuvexa.app.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeContrastTest {
    @Test
    fun darkThemeCorePairsMeetReadableContrast() {
        assertContrast("dark background", OnBackgroundDark, BackgroundDark, 7.0)
        assertContrast("dark surface", OnBackgroundDark, SurfaceDark, 7.0)
        assertContrast("dark secondary text", OnSurfaceVariantDark, SurfaceDark, 4.5)
        assertContrast("dark primary action", IndigoOnPrimaryDark, IndigoPrimaryDark, 4.5)
        assertContrast("dark secondary action", VioletOnSecondaryDark, VioletSecondaryDark, 4.5)
        assertContrast("dark tertiary action", SkyOnTertiaryDark, SkyTertiaryDark, 4.5)
        assertContrast("dark primary container", IndigoOnPrimaryContainerDark, IndigoPrimaryContainerDark, 4.5)
        assertContrast("dark secondary container", VioletOnSecondaryContainerDark, VioletSecondaryContainerDark, 4.5)
        assertContrast("dark tertiary container", SkyOnTertiaryContainerDark, SkyTertiaryContainerDark, 4.5)
    }

    @Test
    fun lightThemeBrandActionsKeepReadableText() {
        assertContrast("light primary action", IndigoOnPrimaryLight, IndigoPrimaryLight, 4.5)
        assertContrast("light secondary action", VioletOnSecondaryLight, VioletSecondaryLight, 4.5)
        assertContrast("light tertiary action", SkyOnTertiaryLight, SkyTertiaryLight, 4.5)
    }

    private fun assertContrast(name: String, foreground: Color, background: Color, minimum: Double) {
        val ratio = contrastRatio(foreground, background)
        assertTrue("$name contrast was $ratio, expected at least $minimum", ratio >= minimum)
    }

    private fun contrastRatio(first: Color, second: Color): Double {
        val l1 = relativeLuminance(first)
        val l2 = relativeLuminance(second)
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun relativeLuminance(color: Color): Double {
        fun linear(channel: Float): Double {
            val value = channel.toDouble()
            return if (value <= 0.04045) {
                value / 12.92
            } else {
                ((value + 0.055) / 1.055).pow(2.4)
            }
        }

        return 0.2126 * linear(color.red) +
            0.7152 * linear(color.green) +
            0.0722 * linear(color.blue)
    }
}
