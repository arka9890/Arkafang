package com.openfang.mobile.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PastelClayThemeTest {

    // -----------------------------------------------------------------------
    // Colors — Tabel 3.1 (light)
    // -----------------------------------------------------------------------

    @Test
    fun lightColors_surfacePrimaryBackgroundOnSurface_matchSpec() {
        val c: ColorScheme = LightColors
        assertThat(c.surface).isEqualTo(Color(0xFFFBF8FF))
        assertThat(c.primary).isEqualTo(Color(0xFF4361EE))
        assertThat(c.background).isEqualTo(Color(0xFFFBF8FF))
        assertThat(c.onSurface).isEqualTo(Color(0xFF0E193E))
    }

    @Test
    fun lightColors_primaryContainerAndOnSurfaceVariant_matchSpec() {
        assertThat(LightColors.primaryContainer).isEqualTo(Color(0xFF4361EE))
        assertThat(LightColors.onSurfaceVariant).isEqualTo(Color(0xFF444655))
        assertThat(LightColors.outline).isEqualTo(Color(0xFF747686))
        assertThat(LightColors.surfaceTint).isEqualTo(Color(0xFF2E4EDC))
    }

    // -----------------------------------------------------------------------
    // Colors — Tabel 3.1 (dark, inverses)
    // -----------------------------------------------------------------------

    @Test
    fun darkColors_surfacePrimaryBackgroundOnSurface_matchSpec() {
        val c: ColorScheme = DarkColors
        assertThat(c.surface).isEqualTo(Color(0xFF0E193E))
        assertThat(c.primary).isEqualTo(Color(0xFFBAC3FF))
        assertThat(c.background).isEqualTo(Color(0xFF0E193E))
        assertThat(c.onSurface).isEqualTo(Color(0xFFEFFFFF))
    }

    @Test
    fun darkColors_inversePrimaryAndError_matchSpec() {
        assertThat(DarkColors.inversePrimary).isEqualTo(Color(0xFF4361EE))
        assertThat(DarkColors.error).isEqualTo(Color(0xFFFF8A80))
        assertThat(DarkColors.onError).isEqualTo(Color(0xFF0E193E))
    }

    // -----------------------------------------------------------------------
    // Typography
    // -----------------------------------------------------------------------

    @Test
    fun typography_isTypographyAndUsesSansSerifFallback() {
        assertThat(PastelClayTypography).isInstanceOf(Typography::class.java)
        // Plus Jakarta Sans TTF is not bundled yet; falls back to system sans-serif.
        assertThat(PastelClayFontFamily).isEqualTo(FontFamily.Default)
    }

    @Test
    fun typography_appliesFontFamilyToHeadlineAndBody() {
        assertThat(PastelClayTypography.headlineLarge.fontFamily)
            .isEqualTo(FontFamily.Default)
        assertThat(PastelClayTypography.bodyMedium.fontFamily)
            .isEqualTo(FontFamily.Default)
        assertThat(PastelClayTypography.labelSmall.fontFamily)
            .isEqualTo(FontFamily.Default)
    }

    // -----------------------------------------------------------------------
    // Shapes
    // -----------------------------------------------------------------------

    @Test
    fun shapes_exposeMaterial3SizesAndClayShapes() {
        assertThat(PastelClayShapes).isInstanceOf(Shapes::class.java)
        assertThat(PastelClayShapes.small).isNotNull()
        assertThat(PastelClayShapes.medium).isNotNull()
        assertThat(PastelClayShapes.large).isNotNull()
    }

    @Test
    fun pillAndRounded3xl_shapes_exist() {
        assertThat(PillShape).isNotNull()
        assertThat(Rounded3XL).isNotNull()
    }
}
