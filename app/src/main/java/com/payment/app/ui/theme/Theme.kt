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
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val primaryContainer: Color,
    val secondaryContainer: Color,
    val tertiaryContainer: Color,
    val darkPrimary: Color,
    val darkSecondary: Color,
    val darkTertiary: Color,
    val darkPrimaryContainer: Color,
    val darkSecondaryContainer: Color,
    val darkTertiaryContainer: Color
)

private val palettes = mapOf(
    "ocean" to ThemePalette(
        primary = Color(0xFF00478D),
        secondary = Color(0xFF526070),
        tertiary = Color(0xFF00523F),
        primaryContainer = Color(0xFF005EB8),
        secondaryContainer = Color(0xFFD5E4F7),
        tertiaryContainer = Color(0xFF036C55),
        darkPrimary = Color(0xFFA9C7FF),
        darkSecondary = Color(0xFFB9C8DA),
        darkTertiary = Color(0xFF9EF3D6),
        darkPrimaryContainer = Color(0xFF00468C),
        darkSecondaryContainer = Color(0xFF3A4857),
        darkTertiaryContainer = Color(0xFF00513F)
    ),
    "emerald" to ThemePalette(
        primary = Color(0xFF1E6E4A),
        secondary = Color(0xFF5A6D60),
        tertiary = Color(0xFF1B6A76),
        primaryContainer = Color(0xFF2A8A5E),
        secondaryContainer = Color(0xFFD4E9DA),
        tertiaryContainer = Color(0xFFC9EAF2),
        darkPrimary = Color(0xFF9FE5C0),
        darkSecondary = Color(0xFFB7CCBE),
        darkTertiary = Color(0xFFAADCE8),
        darkPrimaryContainer = Color(0xFF0D5A37),
        darkSecondaryContainer = Color(0xFF394D41),
        darkTertiaryContainer = Color(0xFF1C5660)
    ),
    "sunset" to ThemePalette(
        primary = Color(0xFF9A4A11),
        secondary = Color(0xFF6E5F52),
        tertiary = Color(0xFF8A3B4D),
        primaryContainer = Color(0xFFB55C1E),
        secondaryContainer = Color(0xFFE8DDD2),
        tertiaryContainer = Color(0xFFF2D3DC),
        darkPrimary = Color(0xFFFFC195),
        darkSecondary = Color(0xFFD8C7B9),
        darkTertiary = Color(0xFFF0B7C6),
        darkPrimaryContainer = Color(0xFF7B3909),
        darkSecondaryContainer = Color(0xFF4F4439),
        darkTertiaryContainer = Color(0xFF6A2B3A)
    ),
    "slate" to ThemePalette(
        primary = Color(0xFF2E536F),
        secondary = Color(0xFF5C6572),
        tertiary = Color(0xFF4C5F83),
        primaryContainer = Color(0xFF3A6689),
        secondaryContainer = Color(0xFFDEE3EA),
        tertiaryContainer = Color(0xFFD7E0F6),
        darkPrimary = Color(0xFFA8C8E6),
        darkSecondary = Color(0xFFC1C8D2),
        darkTertiary = Color(0xFFBCCBEF),
        darkPrimaryContainer = Color(0xFF1F3D53),
        darkSecondaryContainer = Color(0xFF444D59),
        darkTertiaryContainer = Color(0xFF344463)
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
            onPrimary = Color(0xFF001B3D),
            primaryContainer = palette.darkPrimaryContainer,
            onPrimaryContainer = Color(0xFFC8DAFF),
            secondary = palette.darkSecondary,
            onSecondary = Color(0xFF10202E),
            secondaryContainer = palette.darkSecondaryContainer,
            onSecondaryContainer = Color(0xFFD5E4F7),
            tertiary = palette.darkTertiary,
            onTertiary = Color(0xFF002118),
            tertiaryContainer = palette.darkTertiaryContainer,
            onTertiaryContainer = Color(0xFF95EACD),
            background = Color(0xFF171A1F),
            onBackground = Color(0xFFE8EBF2),
            surface = Color(0xFF171A1F),
            onSurface = Color(0xFFE8EBF2),
            surfaceVariant = Color(0xFF2B303A),
            onSurfaceVariant = Color(0xFFC2C6D4),
            error = Color(0xFFFFB4AB),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            outline = Color(0xFF727783)
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = Color.White,
            primaryContainer = palette.primaryContainer,
            onPrimaryContainer = Color(0xFFC8DAFF),
            secondary = palette.secondary,
            onSecondary = Color.White,
            secondaryContainer = palette.secondaryContainer,
            onSecondaryContainer = Color(0xFF586676),
            tertiary = palette.tertiary,
            onTertiary = Color.White,
            tertiaryContainer = palette.tertiaryContainer,
            onTertiaryContainer = Color(0xFF004D3D),
            background = Color(0xFFF9F9FF),
            onBackground = Color(0xFF191C21),
            surface = Color(0xFFF9F9FF),
            onSurface = Color(0xFF191C21),
            surfaceVariant = Color(0xFFE1E2EA),
            onSurfaceVariant = Color(0xFF424752),
            error = Color(0xFFBA1A1A),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF93000A),
            outline = Color(0xFF727783)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

