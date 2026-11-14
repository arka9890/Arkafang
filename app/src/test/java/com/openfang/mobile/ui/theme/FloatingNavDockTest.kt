package com.openfang.mobile.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FloatingNavDockTest {

    // -----------------------------------------------------------------------
    // Spec values FloatingNavDock consumes / exposes
    // -----------------------------------------------------------------------

    @Test
    fun floatingNavTabs_hasThreeTabs() {
        assertThat(FloatingNavTabs).hasSize(3)
        assertThat(FloatingNavTabs).containsExactly("Chat", "Provider", "Pengaturan")
    }

    @Test
    fun floatingNavDockBackdrop_isWhite85Percent() {
        // rgba(255, 255, 255, 0.85) -> alpha byte 0xD9
        assertThat(FloatingNavDockBackdrop).isEqualTo(Color(0xD9FFFFFF))
    }

    @Test
    fun floatingNavTabInactive_isSlate500() {
        assertThat(FloatingNavTabInactive).isEqualTo(Color(0xFF64748B))
    }
}
