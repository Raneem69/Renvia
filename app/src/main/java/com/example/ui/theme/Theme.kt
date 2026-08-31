package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.example.data.FontStyleFamily
import com.example.data.ThemeAesthetic

fun getContrastingTextColor(background: Color): Color {
    return if (background.luminance() > 0.5f) {
        Color(0xFF0F172A) // Dark slate for light backgrounds
    } else {
        Color(0xFFF8FAFC) // Crisp white-slate for dark backgrounds
    }
}

@Composable
fun MindTheme(
    aesthetic: ThemeAesthetic = ThemeAesthetic.ORIGINAL,
    fontStyle: FontStyleFamily = FontStyleFamily.MODERN,
    content: @Composable () -> Unit
) {
    val primary = Color(aesthetic.primaryColorHex)
    val background = Color(aesthetic.backgroundColorHex)
    val surface = Color(aesthetic.surfaceColorHex)
    val secondary = Color(aesthetic.accentColorHex)

    val colorScheme = darkColorScheme(
        primary = primary,
        onPrimary = getContrastingTextColor(primary),
        secondary = secondary,
        onSecondary = getContrastingTextColor(secondary),
        background = background,
        onBackground = getContrastingTextColor(background),
        surface = surface,
        onSurface = getContrastingTextColor(surface),
        surfaceVariant = surface.copy(alpha = 0.8f),
        onSurfaceVariant = Slate400,
        outline = secondary.copy(alpha = 0.25f),
        outlineVariant = Slate700.copy(alpha = 0.4f),
        error = MindError,
        onError = PureWhite
    )

    val typography = createMindTypography(fontStyle)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
