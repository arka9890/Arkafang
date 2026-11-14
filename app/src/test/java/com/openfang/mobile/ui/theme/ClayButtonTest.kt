package com.openfang.mobile.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ClayButtonTest {

    // -----------------------------------------------------------------------
    // Spec values ClayButton consumes (from PastelClayTheme / ClayShadow)
    // -----------------------------------------------------------------------

    @Test
    fun clayButton_fill_isPrimary() {
        assertThat(LightColors.primary).isEqualTo(Color(0xFF4361EE))
    }

    @Test
    fun clayButton_text_isOnPrimaryWhite() {
        assertThat(LightColors.onPrimary).isEqualTo(Color(0xFFFFFFFF))
    }

    @Test
    fun clayButton_shape_isPill() {
        assertThat(PillShape).isNotNull()
    }

    @Test
    fun clayButton_pressed_offset_isReduced() {
        assertThat(clayPressedOuterOffsetDp).isLessThan(clayOuterOffsetDp)
        assertThat(clayPressedOuterOffsetDp).isEqualTo(6f * 0.4f)
    }
}
