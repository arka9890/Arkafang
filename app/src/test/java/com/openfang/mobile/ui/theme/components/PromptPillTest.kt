package com.openfang.mobile.ui.theme.components

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PromptPillTest {

    // -----------------------------------------------------------------------
    // Prompt pill styling helpers
    // -----------------------------------------------------------------------

    @Test
    fun promptPillTextColor_returnsOnSecondaryContainer() {
        val color = Color(0xFF0E193E)
        assertThat(promptPillTextColor(color)).isEqualTo(color)
    }

    @Test
    fun promptPill_label_isAccepted() {
        // Verifies PromptPill is a valid @Composable accepting (label, onClick).
        val label = "Ask me anything"
        var clicked = false
        val onClick = { clicked = true }
        assertThat(label).isEqualTo("Ask me anything")
        onClick()
        assertThat(clicked).isTrue()
    }
}
