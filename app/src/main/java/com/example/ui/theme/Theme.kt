package com.example.ui.theme

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
    primary = WarpOrange,
    onPrimary = Color.White,
    primaryContainer = WarpOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = AppleBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color.White,
    tertiary = WarpCyan,
    background = Color(0xFF0F0F14),
    onBackground = Color.White,
    surface = MacWindowBg,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF262632),
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = MacWindowBorder
)

private val LightColorScheme = darkColorScheme( // Keep sleek dark glass as default for Apple macOS desktop feel
    primary = WarpOrange,
    onPrimary = Color.White,
    secondary = AppleBlue,
    tertiary = WarpCyan,
    background = Color(0xFF0F0F14),
    surface = MacWindowBg
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep macOS aesthetic consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
