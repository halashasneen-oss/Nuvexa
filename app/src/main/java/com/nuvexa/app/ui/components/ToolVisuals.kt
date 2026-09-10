package com.nuvexa.app.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nuvexa.app.core.model.ToolCategory

data class ToolVisualStyle(
    val accent: Color,
    val container: Color,
)

/**
 * Gives each tool family a recognizable visual identity while keeping the palette consistent
 * across light and dark themes. These colors are decorative only; semantic states continue to
 * use the status colors from the theme.
 */
@Composable
fun toolVisualStyle(category: ToolCategory?): ToolVisualStyle {
    val dark = isSystemInDarkTheme()
    val accent = when (category) {
        ToolCategory.CALCULATORS -> Color(0xFF2563EB)
        ToolCategory.CONVERTER -> Color(0xFF0891B2)
        ToolCategory.CURRENCY -> Color(0xFF059669)
        ToolCategory.TEXT -> Color(0xFF7C3AED)
        ToolCategory.SECURITY -> Color(0xFFD97706)
        ToolCategory.QR -> Color(0xFF0F766E)
        ToolCategory.COLOR -> Color(0xFFDB2777)
        ToolCategory.TIME -> Color(0xFF0D9488)
        ToolCategory.DEVELOPER -> Color(0xFF4F46E5)
        ToolCategory.DEVICE -> Color(0xFF475569)
        ToolCategory.IMAGE -> Color(0xFFC026D3)
        ToolCategory.MATH -> Color(0xFF9333EA)
        ToolCategory.PDF_DOCUMENT -> Color(0xFFDC2626)
        ToolCategory.OCR -> Color(0xFFEA580C)
        null -> Color(0xFF4F46E5)
    }
    return ToolVisualStyle(
        accent = if (dark) accent.copy(alpha = 0.95f) else accent,
        container = accent.copy(alpha = if (dark) 0.22f else 0.11f),
    )
}
