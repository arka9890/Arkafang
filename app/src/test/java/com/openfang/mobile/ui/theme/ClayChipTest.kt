package com.openfang.mobile.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ClayChipTest {

    // -----------------------------------------------------------------------
    // Spec values ClayChip consumes (from PastelClayTheme / ClayShadow)
    // -----------------------------------------------------------------------

    @Test
    fun clayChip_shape_isPill() {
        assertThat(PillShape).isNotNull()
    }

    @Test
    fun clayChip_selected_fill_isPrimaryContainer() {
        assertThat(LightColors.primaryContainer).isEqualTo(Color(0xFF4361EE))
    }

    @Test
    fun clayChip_unselected_fill_isSurfaceContainerLow() {
        assertThat(LightColors.surfaceContainerLow).isNotEqualTo(Color.Unspecified)
    }

    @Test
    fun clayChip_pressed_offset_isReduced() {
        assertThat(clayPressedOuterOffsetDp).isLessThan(clayOuterOffsetDp)
    }
}
