
package com.example.health.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorForLightSurface = Color(0xFFEEEEEE)

private val DarkScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    background = PureBlack,
    onBackground = PureWhite,
    surface = DarkSurface,
    onSurface = PureWhite,
    surfaceVariant = ElevatedSurface,
    onSurfaceVariant = SoftGray,
    outline = BorderGray
)

private val LightScheme = lightColorScheme(
    primary = PureBlack,
    onPrimary = PureWhite,
    background = BackgroundWhite,
    onBackground = PureBlack,
    surface = PureWhite,
    onSurface = PureBlack,
    surfaceVariant = ColorForLightSurface,
    onSurfaceVariant = MutedGray,
    outline = BorderGray
)

@Composable
fun HealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = HealthTypography,
        content = content
    )
}
