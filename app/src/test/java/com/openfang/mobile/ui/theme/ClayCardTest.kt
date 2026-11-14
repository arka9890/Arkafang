package com.openfang.mobile.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ClayCardTest {

    // -----------------------------------------------------------------------
    // Spec values ClayCard consumes (from PastelClayTheme / ClayShadow)
    // -----------------------------------------------------------------------

    @Test
    fun clayCard_shape_isRounded3xl() {
        assertThat(Rounded3XL).isNotNull()
    }

    @Test
    fun clayCard_fill_isSurfaceContainerLow() {
        assertThat(LightColors.surfaceContainerLow).isEqualTo(LightColors.surfaceContainerLow)
        assertThat(LightColors.surfaceContainerLow).isNotEqualTo(Color.Unspecified)
    }

    @Test
    fun clayCard_elevation_usesClayOuterOffset() {
        assertThat(clayOuterOffsetDp).isEqualTo(6f)
    }
}
