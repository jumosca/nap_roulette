package com.naproulette.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VintageColorScheme = lightColorScheme(
    primary = CinnabarRed,
    onPrimary = VintagePaper,
    primaryContainer = CinnabarRedFaint,
    onPrimaryContainer = CinnabarRedDark,
    secondary = VintageGold,
    onSecondary = InkBlack,
    background = VintagePaper,
    onBackground = InkBlack,
    surface = VintagePaper,
    onSurface = InkBlack,
    surfaceVariant = VintageCard,
    onSurfaceVariant = InkDark,
    error = CinnabarRed,
    onError = VintagePaper,
    outline = InkFaint,
    outlineVariant = VintageBorder
)

@Composable
fun NapRouletteTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = VintagePaper.toArgb()
            window.navigationBarColor = VintagePaper.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = VintageColorScheme,
        typography = NapTypography,
        content = content
    )
}
