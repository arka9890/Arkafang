package com.openfang.mobile.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ClayToggleTest {

    // -----------------------------------------------------------------------
    // Spec values ClayToggle consumes (from PastelClayTheme / ClayShadow)
    // -----------------------------------------------------------------------

    @Test
    fun clayToggle_shape_isPill() {
        assertThat(PillShape).isNotNull()
    }

    @Test
    fun clayToggle_selected_fill_isPrimary() {
        assertThat(LightColors.primary).isEqualTo(Color(0xFF4361EE))
    }

    @Test
    fun clayToggle_unselected_fill_isSurfaceContainerLow() {
        assertThat(LightColors.surfaceContainerLow).isNotEqualTo(Color.Unspecified)
    }

    @Test
    fun clayToggle_thumb_usesClayElevation() {
        assertThat(clayOuterOffsetDp).isEqualTo(6f)
    }
}
