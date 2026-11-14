package com.openfang.mobile.ui.theme

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SidebarDrawerTest {

    // -----------------------------------------------------------------------
    // Spec values SidebarDrawer consumes / exposes
    // -----------------------------------------------------------------------

    @Test
    fun sidebarDrawerWidthFraction_is84Percent() {
        assertThat(SidebarDrawerWidthFraction).isEqualTo(0.84f)
    }

    @Test
    fun sidebarDrawerMaxWidth_is340dp() {
        assertThat(SidebarDrawerMaxWidthDp).isEqualTo(340f)
    }
}
