package com.naproulette.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Serif for headings — Victorian / Art Deco feel
val VintageSerif = FontFamily.Serif

// Clean sans-serif for UI elements and numbers
val VintageSans = FontFamily.SansSerif

val NapTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = VintageSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 72.sp,
        lineHeight = 80.sp,
        letterSpacing = (-1).sp,
        color = InkBlack
    ),
    displayMedium = TextStyle(
        fontFamily = VintageSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.sp,
        color = InkBlack
    ),
    headlineLarge = TextStyle(
        fontFamily = VintageSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 1.sp,
        color = InkBlack
    ),
    headlineMedium = TextStyle(
        fontFamily = VintageSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
        color = InkBlack
    ),
    titleLarge = TextStyle(
        fontFamily = VintageSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = InkBlack
    ),
    bodyLarge = TextStyle(
        fontFamily = VintageSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = InkDark
    ),
    bodyMedium = TextStyle(
        fontFamily = VintageSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = InkMedium
    ),
    labelLarge = TextStyle(
        fontFamily = VintageSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 2.sp,
        color = InkBlack
    ),
    labelMedium = TextStyle(
        fontFamily = VintageSans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.sp,
        color = InkMedium
    )
)
