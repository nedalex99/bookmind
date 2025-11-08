package com.nedalex.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val LightColorScheme = lightColorScheme(
    // Primary - Stone 900 (Dark)
    primary = Stone900,
    onPrimary = Stone50,
    primaryContainer = Stone100,
    onPrimaryContainer = Stone900,

    // Secondary - Stone 700
    secondary = Stone700,
    onSecondary = Stone50,
    secondaryContainer = Stone200,
    onSecondaryContainer = Stone900,

    // Tertiary - Emerald (Success/Accent)
    tertiary = Emerald600,
    onTertiary = Stone50,
    tertiaryContainer = Emerald50,
    onTertiaryContainer = Emerald600,

    // Error
    error = Red600,
    onError = Color.White,
    errorContainer = Red50,
    onErrorContainer = Red600,

    // Background
    background = Stone50,
    onBackground = Stone900,

    // Surface
    surface = Color.White,
    onSurface = Stone900,
    surfaceVariant = Stone100,
    onSurfaceVariant = Stone700,

    // Outline
    outline = Stone200,
    outlineVariant = Stone100,

    // Inverse
    inverseSurface = Stone900,
    inverseOnSurface = Stone50,
    inversePrimary = Stone200
)

val DarkColorScheme = darkColorScheme(
    primary = Stone100,
    onPrimary = Stone900,
    primaryContainer = Stone800,
    onPrimaryContainer = Stone100,

    secondary = Stone300,
    onSecondary = Stone900,
    secondaryContainer = Stone800,
    onSecondaryContainer = Stone100,

    tertiary = Emerald500,
    onTertiary = Stone900,

    error = Color(0xFFEF4444),
    onError = Stone900,

    background = Stone900,
    onBackground = Stone100,

    surface = Stone800,
    onSurface = Stone100,
    surfaceVariant = Stone700,
    onSurfaceVariant = Stone300,

    outline = Stone600,
    outlineVariant = Stone700
)

val AppTypography = Typography(
    // Display (Georgia-like serif for book titles)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Light,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Light,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Light,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),

    // Headings
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Light,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),

    // Title
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),

    // Body
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Labels
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun ReadingAppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}