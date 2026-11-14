package com.openfang.mobile.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

/**
 * Pastel Clay design system theme wrapper.
 *
 * Usage:
 * ```
 * PastelClayTheme(isDark = isDark) {
 *   // your Compose UI
 * }
 * ```
 *
 * Colors follow Tabel 3.1 of the Pastel Clay design doc:
 *  - Light: surface #fbf8ff, primary #4361EE, background #fbf8ff, on-surface #0e193e
 *  - Dark:  surface #0e193e, primary #bac3ff, background #0e193e, on-surface #efefff
 *
 * Typography: Plus Jakarta Sans, falling back to the system sans-serif
 * (FontFamily.Default) until the Plus Jakarta Sans TTF is added to res/font/.
 */

/** Packs an ARGB hex literal into a Compose [Color]. */
private fun clayColor(hex: Long) = Color(hex.toInt())

// ---------------------------------------------------------------------------
// Light palette (Tabel 3.1)
// ---------------------------------------------------------------------------
val LightColors = ColorScheme(
    primary = clayColor(0xFF4361EE),
    onPrimary = clayColor(0xFFFFFFFF),
    primaryContainer = clayColor(0xFF4361EE),
    onPrimaryContainer = clayColor(0xFFF4F2FF),
    inversePrimary = clayColor(0xFFBAC3FF),
    secondary = clayColor(0xFF3F5AAB),
    onSecondary = clayColor(0xFFFFFFFF),
    secondaryContainer = clayColor(0xFF8EA8FF),
    onSecondaryContainer = clayColor(0xFF1A398A),
    tertiary = clayColor(0xFF435878),
    onTertiary = clayColor(0xFFFFFFFF),
    tertiaryContainer = clayColor(0xFF5C7091),
    onTertiaryContainer = clayColor(0xFFF0F3FF),
    background = clayColor(0xFFFBF8FF),
    onBackground = clayColor(0xFF0E193E),
    surface = clayColor(0xFFFBF8FF),
    onSurface = clayColor(0xFF0E193E),
    surfaceVariant = clayColor(0xFFDDE1FF),
    onSurfaceVariant = clayColor(0xFF444655),
    surfaceTint = clayColor(0xFF2E4EDC),
    inverseSurface = clayColor(0xFF242E55),
    inverseOnSurface = clayColor(0xFFEFFFFF),
    error = clayColor(0xFFBA1A1A),
    onError = clayColor(0xFFFFFFFF),
    errorContainer = clayColor(0xFFFFDAD6),
    onErrorContainer = clayColor(0xFF93000A),
    outline = clayColor(0xFF747686),
    outlineVariant = clayColor(0xFFC4C5D7),
    scrim = clayColor(0x1A000000),
)

// ---------------------------------------------------------------------------
// Dark palette (Tabel 3.1 — inverses)
// ---------------------------------------------------------------------------
val DarkColors = ColorScheme(
    primary = clayColor(0xFFBAC3FF),
    onPrimary = clayColor(0xFF0E193E),
    primaryContainer = clayColor(0xFF2E4EDC),
    onPrimaryContainer = clayColor(0xFF1A2A5C),
    inversePrimary = clayColor(0xFF4361EE),
    secondary = clayColor(0xFF8EA8FF),
    onSecondary = clayColor(0xFF0E193E),
    secondaryContainer = clayColor(0xFF3F5AAB),
    onSecondaryContainer = clayColor(0xFF0E193E),
    tertiary = clayColor(0xFF5C7091),
    onTertiary = clayColor(0xFFF0F3FF),
    tertiaryContainer = clayColor(0xFF435878),
    onTertiaryContainer = clayColor(0xFF1A2A5C),
    background = clayColor(0xFF0E193E),
    onBackground = clayColor(0xFFEFFFFF),
    surface = clayColor(0xFF0E193E),
    onSurface = clayColor(0xFFEFFFFF),
    surfaceVariant = clayColor(0xFF1E3066),
    onSurfaceVariant = clayColor(0xFFC4C5D7),
    surfaceTint = clayColor(0xFFBAC3FF),
    inverseSurface = clayColor(0xFFFBF8FF),
    inverseOnSurface = clayColor(0xFF0E193E),
    error = clayColor(0xFFFF8A80),
    onError = clayColor(0xFF0E193E),
    errorContainer = clayColor(0xFFBA1A1A),
    onErrorContainer = clayColor(0xFF0E193E),
    outline = clayColor(0xFF5A6882),
    outlineVariant = clayColor(0xFF3A4A7C),
    scrim = clayColor(0x1AFFFFFF),
)

// ---------------------------------------------------------------------------
// Typography
// ---------------------------------------------------------------------------

/**
 * Font family used across the Pastel Clay type scale.
 *
 * Plus Jakarta Sans must be added as `res/font/plus_jakarta_sans.ttf` (plus a
 * `res/font/plus_jakarta_sans.xml` family) before this can point at it. Until
 * then we fall back to the system sans-serif via [FontFamily.Default], which
 * satisfies the "fallback to sans-serif if TTF unavailable" requirement.
 */
val PastelClayFontFamily: FontFamily = FontFamily.Default

private fun clayTextStyle(
    fontSize: TextUnit = TextUnit(16f, TextUnitType.Sp),
    fontWeight: FontWeight = FontWeight.Normal,
): TextStyle = TextStyle(
    fontFamily = PastelClayFontFamily,
    fontSize = fontSize,
    fontWeight = fontWeight,
)

val PastelClayTypography = Typography(
    displayLarge = clayTextStyle(TextUnit(57f, TextUnitType.Sp), FontWeight.W400),
    displayMedium = clayTextStyle(TextUnit(45f, TextUnitType.Sp), FontWeight.W400),
    displaySmall = clayTextStyle(TextUnit(36f, TextUnitType.Sp), FontWeight.W400),
    headlineLarge = clayTextStyle(TextUnit(32f, TextUnitType.Sp), FontWeight.W600),
    headlineMedium = clayTextStyle(TextUnit(28f, TextUnitType.Sp), FontWeight.W600),
    headlineSmall = clayTextStyle(TextUnit(24f, TextUnitType.Sp), FontWeight.W600),
    titleLarge = clayTextStyle(TextUnit(22f, TextUnitType.Sp), FontWeight.W600),
    titleMedium = clayTextStyle(TextUnit(16f, TextUnitType.Sp), FontWeight.W600),
    titleSmall = clayTextStyle(TextUnit(14f, TextUnitType.Sp), FontWeight.W600),
    bodyLarge = clayTextStyle(TextUnit(16f, TextUnitType.Sp), FontWeight.W400),
    bodyMedium = clayTextStyle(TextUnit(14f, TextUnitType.Sp), FontWeight.W400),
    bodySmall = clayTextStyle(TextUnit(12f, TextUnitType.Sp), FontWeight.W400),
    labelLarge = clayTextStyle(TextUnit(14f, TextUnitType.Sp), FontWeight.W600),
    labelMedium = clayTextStyle(TextUnit(12f, TextUnitType.Sp), FontWeight.W600),
    labelSmall = clayTextStyle(TextUnit(11f, TextUnitType.Sp), FontWeight.W600),
)

// ---------------------------------------------------------------------------
// Shapes
// ---------------------------------------------------------------------------

val PastelClayShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(32.dp),
)

/** Fully rounded pill shape (effectively infinite corner radius). */
val PillShape = RoundedCornerShape(9999.dp)

/** Rounded-3xl (32dp) for cards and containers. */
val Rounded3XL = RoundedCornerShape(32.dp)

// ---------------------------------------------------------------------------
// Theme composable
// ---------------------------------------------------------------------------

/**
 * Pastel Clay theme wrapper. Provides [ColorScheme], [Typography] and [Shapes]
 * for the Pastel Clay design system. Wraps [MaterialTheme] so existing
 * Material3 components continue to work under the Pastel Clay look.
 *
 * @param isDark whether to use the dark palette. Preference is persisted
 * elsewhere (SharedPreferences) and passed in by the caller.
 */
@Composable
fun PastelClayTheme(
    isDark: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isDark) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PastelClayTypography,
        shapes = PastelClayShapes,
        content = content,
    )
}
