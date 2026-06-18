package com.onur.oleai_android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkPrimaryFg,
    primaryContainer = Color(0xFF2D3B1E),
    onPrimaryContainer = Color(0xFFC5D8A8),
    secondary = DarkSecondary,
    onSecondary = DarkSecondaryFg,
    background = DarkBackground,
    onBackground = DarkForeground,
    surface = DarkCard,
    onSurface = DarkForeground,
    surfaceVariant = DarkMuted,
    onSurfaceVariant = DarkMutedFg,
    error = LightDestructive,
    onError = Color.White,
    outline = DarkBorder,
    outlineVariant = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightPrimaryFg,
    primaryContainer = Color(0xFFDAE8C8),
    onPrimaryContainer = Color(0xFF2D3B1E),
    secondary = LightSecondary,
    onSecondary = LightSecondaryFg,
    background = LightBackground,
    onBackground = LightForeground,
    surface = LightCard,
    onSurface = LightForeground,
    surfaceVariant = LightMuted,
    onSurfaceVariant = LightMutedFg,
    error = LightDestructive,
    onError = Color.White,
    outline = LightBorder,
    outlineVariant = LightBorder
)

@Composable
fun OleAIandroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is DISABLED by default to enforce our custom design system
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes, // Enforcing custom shapes
        content = content
    )
}