package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.data.FontStyleFamily

fun createMindTypography(fontStyle: FontStyleFamily = FontStyleFamily.MODERN): Typography {
    val baseFamily = when (fontStyle) {
        FontStyleFamily.MINIMAL -> FontFamily.Monospace
        FontStyleFamily.ROUNDED -> FontFamily.SansSerif
        FontStyleFamily.ELEGANT -> FontFamily.Serif
        FontStyleFamily.ACCESSIBILITY -> FontFamily.Default
        FontStyleFamily.MODERN -> FontFamily.Default
    }

    val baseWeight = if (fontStyle == FontStyleFamily.ACCESSIBILITY) FontWeight.Medium else FontWeight.Normal

    return Typography(
        displayLarge = TextStyle(
            fontFamily = baseFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.5).sp,
            color = Slate100
        ),
        displayMedium = TextStyle(
            fontFamily = baseFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = (-0.25).sp,
            color = Slate100
        ),
        headlineLarge = TextStyle(
            fontFamily = baseFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            color = Slate100
        ),
        headlineMedium = TextStyle(
            fontFamily = baseFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            color = Slate100
        ),
        titleLarge = TextStyle(
            fontFamily = baseFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp,
            color = Slate100
        ),
        titleMedium = TextStyle(
            fontFamily = baseFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.15.sp,
            color = Slate100
        ),
        titleSmall = TextStyle(
            fontFamily = baseFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Slate300
        ),
        bodyLarge = TextStyle(
            fontFamily = baseFamily,
            fontWeight = baseWeight,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.25.sp,
            color = Slate100
        ),
        bodyMedium = TextStyle(
            fontFamily = baseFamily,
            fontWeight = baseWeight,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.2.sp,
            color = Slate300
        ),
        bodySmall = TextStyle(
            fontFamily = baseFamily,
            fontWeight = baseWeight,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = Slate400
        ),
        labelLarge = TextStyle(
            fontFamily = baseFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.5.sp,
            color = Slate100
        ),
        labelMedium = TextStyle(
            fontFamily = baseFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.5.sp,
            color = Slate400
        ),
        labelSmall = TextStyle(
            fontFamily = baseFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            letterSpacing = 0.5.sp,
            color = Slate400
        )
    )
}
