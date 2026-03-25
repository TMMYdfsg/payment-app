package com.payment.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private data class ThemePalette(
    val lightPrimary: Color,
    val lightSecondary: Color,
    val lightTertiary: Color,
    val darkPrimary: Color,
    val darkSecondary: Color,
    val darkTertiary: Color
)

private val palettes = mapOf(
    "ocean" to ThemePalette(
        lightPrimary = Color(0xFF005F8F),
        lightSecondary = Color(0xFF2B7899),
        lightTertiary = Color(0xFF3A8C8E),
        darkPrimary = Color(0xFF7BC7FF),
        darkSecondary = Color(0xFF9DCCE4),
        darkTertiary = Color(0xFFA7DCCF)
    ),
    "emerald" to ThemePalette(
        lightPrimary = Color(0xFF146C43),
        lightSecondary = Color(0xFF3E7D5A),
        lightTertiary = Color(0xFF4A8A66),
        darkPrimary = Color(0xFF7DDAA3),
        darkSecondary = Color(0xFFA4D8BC),
        darkTertiary = Color(0xFFB5E3C8)
    ),
    "sunset" to ThemePalette(
        lightPrimary = Color(0xFFB24A00),
        lightSecondary = Color(0xFF9F5F2E),
        lightTertiary = Color(0xFFC35E3A),
        darkPrimary = Color(0xFFFFB68A),
        darkSecondary = Color(0xFFEDC2A7),
        darkTertiary = Color(0xFFFFC6AA)
    ),
    "slate" to ThemePalette(
        lightPrimary = Color(0xFF2C4A63),
        lightSecondary = Color(0xFF4A6072),
        lightTertiary = Color(0xFF5D6F83),
        darkPrimary = Color(0xFFA6C7E6),
        darkSecondary = Color(0xFFB5C8DA),
        darkTertiary = Color(0xFFC5D5E6)
    )
)

@Composable
fun PaymentAppTheme(
    themeMode: String = "system",
    accent: String = "ocean",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val palette = palettes[accent] ?: palettes.getValue("ocean")

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = palette.darkPrimary,
            secondary = palette.darkSecondary,
            tertiary = palette.darkTertiary
        )
    } else {
        lightColorScheme(
            primary = palette.lightPrimary,
            secondary = palette.lightSecondary,
            tertiary = palette.lightTertiary
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

