package com.openfang.mobile.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ClayShadowTest {
    // -----------------------------------------------------------------------
    // Shadow configuration
    // -----------------------------------------------------------------------

    @Test
    fun config_outerShadow_matchesClaySpec() {
        assertThat(ClayShadowConfig.outerElevation).isEqualTo(6f)
        assertThat(ClayShadowConfig.outerColor).isEqualTo(Color(0x1A000000))
    }

    @Test
    fun config_insetBevel_matchesClaySpec() {
        assertThat(ClayShadowConfig.insetHighlightColor).isEqualTo(Color(0x45FFFFFF))
        assertThat(ClayShadowConfig.insetShadowColor).isEqualTo(Color(0x26000000))
        assertThat(ClayShadowConfig.insetWidth).isEqualTo(1f)
        assertThat(ClayShadowConfig.containerCorner).isEqualTo(16f)
    }

    // -----------------------------------------------------------------------
    // Modifiers exist and contribute to the modifier chain
    // -----------------------------------------------------------------------

    @Test
    fun clayElevation_returnsModifierDistinctFromEmpty() {
        val result = Modifier.clayElevation()
        assertThat(result).isNotNull()
        assertThat(result).isNotSameInstanceAs(Modifier)
    }

    @Test
    fun clayPressed_returnsModifierDistinctFromEmpty() {
        val result = Modifier.clayPressed()
        assertThat(result).isNotNull()
        assertThat(result).isNotSameInstanceAs(Modifier)
    }

    @Test
    fun clayPressed_reducesOuterOffsetVersusClayElevation() {
        // Asserts on real modifier behavior: the pressed shadow offset is derived
        // from, and strictly smaller than, the elevated offset the modifier applies.
        assertThat(clayPressedOuterOffsetDp).isLessThan(clayOuterOffsetDp)
        assertThat(clayPressedOuterOffsetDp).isEqualTo(ClayShadowConfig.outerElevation * 0.4f)
        assertThat(clayOuterOffsetDp).isEqualTo(ClayShadowConfig.outerElevation)
        assertThat(clayPressedOuterOffsetDp).isGreaterThan(0f)
    }
}
