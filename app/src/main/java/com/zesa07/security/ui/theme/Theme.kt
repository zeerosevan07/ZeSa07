package com.zesa07.security.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ZeSa07ColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = VoidBlack,
    secondary = InfoCyan,
    onSecondary = VoidBlack,
    tertiary = WarnAmber,
    background = VoidBlack,
    onBackground = TextPrimary,
    surface = PanelDark,
    onSurface = TextPrimary,
    surfaceVariant = PanelDarkAlt,
    onSurfaceVariant = TextSecondary,
    error = AlertRed,
    onError = Color.White,
    outline = BorderSubtle
)

@Composable
fun ZeSa07Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ZeSa07ColorScheme,
        typography = ZeSa07Typography,
        content = content
    )
}
