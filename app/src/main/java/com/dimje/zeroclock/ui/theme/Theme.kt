package com.dimje.zeroclock.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MoonBlue,
    onPrimary = NightBackground,
    primaryContainer = MoonBlueContainer,
    onPrimaryContainer = Starlight,
    secondary = MutedStarlight,
    background = NightBackground,
    onBackground = Starlight,
    surface = NightSurface,
    onSurface = Starlight,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = MutedStarlight,
    error = WarmAccent,
)

@Composable
fun ZeroClockTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
