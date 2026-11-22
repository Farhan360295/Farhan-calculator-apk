package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CalculatorPalette(
    val isDark: Boolean,
    val canvas: Color,
    val calculatorSurface: Color,
    val elevatedSurface: Color,
    val displayWell: Color,
    val hairline: Color,
    val keyStandard: Color,
    val keyOperator: Color,
    val keyScientific: Color,
    val keyClear: Color,
    val keyStandardInk: Color,
    val keyOperatorInk: Color,
    val keyScientificInk: Color,
    val keyClearInk: Color,
    val equalsGradientStart: Color,
    val equalsGradientEnd: Color,
    val brandAccentStart: Color,
    val brandAccentEnd: Color,
    val statusMuted: Color,
    val statusSuccess: Color,
    val statusError: Color,
)

private val DarkCalculatorPalette = CalculatorPalette(
    isDark = true,
    canvas = MidnightCanvas,
    calculatorSurface = NavySurface,
    elevatedSurface = NavySurfaceElevated,
    displayWell = DisplayWellDark,
    hairline = HairlineDark,
    keyStandard = KeyStandardDark,
    keyOperator = KeyOperatorDark,
    keyScientific = KeyScientificDark,
    keyClear = KeyClearDark,
    keyStandardInk = TextPrimaryDark,
    keyOperatorInk = OperatorInkDark,
    keyScientificInk = OperatorInkDark,
    keyClearInk = ClearInkDark,
    equalsGradientStart = AccentIndigoDeep,
    equalsGradientEnd = AccentVioletDeep,
    brandAccentStart = AccentIndigo,
    brandAccentEnd = AccentViolet,
    statusMuted = TextMutedDark,
    statusSuccess = SuccessInkDark,
    statusError = ErrorInkDark,
)

private val LightCalculatorPalette = CalculatorPalette(
    isDark = false,
    canvas = DaylightCanvas,
    calculatorSurface = DaylightSurface,
    elevatedSurface = DaylightSurfaceElevated,
    displayWell = DisplayWellLight,
    hairline = HairlineLight,
    keyStandard = KeyStandardLight,
    keyOperator = KeyOperatorLight,
    keyScientific = KeyScientificLight,
    keyClear = KeyClearLight,
    keyStandardInk = TextPrimaryLight,
    keyOperatorInk = OperatorInkLight,
    keyScientificInk = OperatorInkLight,
    keyClearInk = ClearInkLight,
    equalsGradientStart = AccentIndigoLight,
    equalsGradientEnd = AccentVioletLight,
    brandAccentStart = AccentIndigoLight,
    brandAccentEnd = AccentVioletLight,
    statusMuted = TextMutedLight,
    statusSuccess = SuccessInkLight,
    statusError = ErrorInkLight,
)

val LocalCalculatorPalette = staticCompositionLocalOf { DarkCalculatorPalette }

private val FarhanDarkColorScheme = darkColorScheme(
    primary = AccentIndigo,
    onPrimary = MidnightCanvas,
    primaryContainer = KeyOperatorDark,
    onPrimaryContainer = OperatorInkDark,
    secondary = AccentViolet,
    onSecondary = MidnightCanvas,
    secondaryContainer = KeyScientificDark,
    onSecondaryContainer = OperatorInkDark,
    tertiary = ClearInkDark,
    onTertiary = MidnightCanvas,
    tertiaryContainer = KeyClearDark,
    onTertiaryContainer = ClearInkDark,
    background = MidnightCanvas,
    onBackground = TextPrimaryDark,
    surface = NavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = NavySurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = HairlineDark,
    error = ErrorInkDark,
    onError = MidnightCanvas,
)

private val FarhanLightColorScheme = lightColorScheme(
    primary = AccentIndigoLight,
    onPrimary = Color.White,
    primaryContainer = KeyOperatorLight,
    onPrimaryContainer = OperatorInkLight,
    secondary = AccentVioletLight,
    onSecondary = Color.White,
    secondaryContainer = KeyScientificLight,
    onSecondaryContainer = OperatorInkLight,
    tertiary = ClearInkLight,
    onTertiary = Color.White,
    tertiaryContainer = KeyClearLight,
    onTertiaryContainer = ClearInkLight,
    background = DaylightCanvas,
    onBackground = TextPrimaryLight,
    surface = DaylightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = DaylightSurfaceElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = HairlineLight,
    error = ErrorInkLight,
    onError = Color.White,
)

@Composable
fun FarhanCalculatorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) FarhanDarkColorScheme else FarhanLightColorScheme
    val palette = if (darkTheme) DarkCalculatorPalette else LightCalculatorPalette

    CompositionLocalProvider(LocalCalculatorPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
