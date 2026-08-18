package com._864zeros.clearstreak.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = OIASageLight,
    onPrimary = OIADarkBg,
    primaryContainer = OIASageDark,
    onPrimaryContainer = OIAOffWhite,
    secondary = OIACoralDark,
    onSecondary = OIADarkBg,
    tertiary = OIADustyBlue,
    background = OIADarkBg,
    onBackground = OIAOffWhite,
    surface = OIADarkCard,
    onSurface = OIAOffWhite,
    surfaceVariant = OIADarkElevated,
    onSurfaceVariant = OIAWarmGray,
    outline = OIAOlive,
    error = OIAError,
    onError = OIADarkBg
)

private val LightColorScheme = lightColorScheme(
    primary = OIASage,
    onPrimary = OIAWarmWhite,
    primaryContainer = OIASageLight,
    onPrimaryContainer = OIACharcoal,
    secondary = OIACoral,
    onSecondary = OIAWarmWhite,
    tertiary = OIADustyBlue,
    background = OIACream,
    onBackground = OIACharcoal,
    surface = OIAWarmWhite,
    onSurface = OIACharcoal,
    surfaceVariant = OIACream,
    onSurfaceVariant = OIAStone,
    outline = OIATaupe,
    error = OIAError,
    onError = OIAWarmWhite
)

@Composable
fun ClearStreakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OIATypography,
        shapes = OIAShapes,
        content = content
    )
}
