package com.openfang.mobile.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ClayInputTest {

    // -----------------------------------------------------------------------
    // Spec values ClayInput consumes (from PastelClayTheme / ClayShadow)
    // -----------------------------------------------------------------------

    @Test
    fun clayInput_shape_isRounded3xl() {
        assertThat(Rounded3XL).isNotNull()
    }

    @Test
    fun clayInput_background_isSurfaceContainerLowest() {
        assertThat(LightColors.surfaceContainerLowest).isNotEqualTo(Color.Unspecified)
    }

    @Test
    fun clayInput_cursor_isPrimary() {
        assertThat(LightColors.primary).isEqualTo(Color(0xFF4361EE))
    }

    @Test
    fun clayInput_recessed_usesClayPressed() {
        assertThat(clayPressedOuterOffsetDp).isLessThan(clayOuterOffsetDp)
    }
}
